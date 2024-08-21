package school.faang.user_service.service;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Address;
import com.json.student.ContactInfo;
import com.json.student.Person;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.event.ProfileViewEventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.image.ImageMapper;
import school.faang.user_service.publisher.ProfileViewMessagePublisher;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.s3.S3Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PersonToUserMapper personToUserMapper;
    private final CountryRepository countryRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorshipService;
    private final ProfileViewMessagePublisher profileViewMessagePublisher;

    private final S3Service s3Service;
    private final ImageMapper imageMapper;

    @Value("${services.frofilePic.maxImagePicture}")
    private final static int MAX_IMAGE_PIC = 1080;
    @Value("${services.frofilePic.minImagePicture}")
    private final static int MIN_IMAGE_PIC = 170;


    public UserDto findUserById(long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    public List<UserDto> findUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds).stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Async
    public CompletableFuture<List<UserDto>> saveStudents(InputStream inputStream) throws IOException {
        log.info("Start to save students from CSV.");
        var persons = readPersonsFromCsv(inputStream);
        log.info("Read {} persons from CSV.", persons.size());

        createCountriesIfNotExist(persons);

        var savedUsers = savePersons(persons);
        log.info("Saved {} users.", savedUsers.size());

        return CompletableFuture.completedFuture(savedUsers.stream()
                .map(userMapper::toDto)
                .toList());

    }

    private List<Person> readPersonsFromCsv(@NotNull InputStream inputStream) throws IOException {
        if (inputStream.available() == 0) {
            log.warn("InputStream is empty.");
            throw new IllegalArgumentException("InputStream is empty.");
        }

        log.info("Reading persons from CSV input stream.");
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        return csvMapper.readerFor(Person.class)
                .with(schema)
                .<Person>readValues(inputStream)
                .readAll();
    }

    private List<User> savePersons(List<Person> persons) {
        log.info("Saving persons as users.");
        return userRepository.saveAll(
                persons.stream()
                        .map(personToUserMapper::mapToUser)
                        .toList()
        );
    }

    private void createCountriesIfNotExist(List<Person> persons) {
        log.info("Creating countries if they do not exist.");
        Set<String> existingCountries = fetchExistingCountryTitles();

        List<Country> newCountries = findNewCountries(persons, existingCountries);
        log.info("Found {} new countries to be saved.", newCountries.size());

        countryRepository.saveAll(newCountries);
    }

    private Set<String> fetchExistingCountryTitles() {
        log.info("Fetching all existing country names from the repository.");
        return countryRepository.findAll().stream()
                .map(Country::getTitle)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private List<Country> findNewCountries(List<Person> persons, Set<String> existingCountries) {
        log.info("Finding new countries from persons.");
        return persons.stream()
                .map(person -> ofNullable(person.getContactInfo())
                        .map(ContactInfo::getAddress)
                        .map(Address::getCountry)
                        .map(String::toLowerCase)
                        .orElse(null))
                .filter(Objects::nonNull)
                .distinct()
                .filter(countryName -> !existingCountries.contains(countryName))
                .map(countryName -> Country.builder().title(countryName).build())
                .toList();
    }

    @Transactional
    public UserDto deactivateUserById(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        stopUserGoals(user);
        deleteUserEvents(user);
        user.setActive(false);
        mentorshipService.stopUserMentorship(user.getId());

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    private void stopUserGoals(User user) {
        for (Goal goal : user.getGoals()) {
            var userList = goal.getUsers();
            if (userList != null) {
                if (userList.size() == 1 && userList.get(0).getId() == user.getId()) {
                    goalRepository.delete(goal);
                } else {
                    userList.remove(user);
                    goalRepository.save(goal);
                }
            }
        }
    }

    private void deleteUserEvents(User user) {
        eventRepository.deleteAll(user.getParticipatedEvents());
    }

    public UserProfilePicDto addUserPic(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));

        UserProfilePic userProfilePic = new UserProfilePic();

        userProfilePic.setFileId(s3Service.uploadFile(imageMapper.convertFilePermissions(file, MAX_IMAGE_PIC)));
        userProfilePic.setSmallFileId(s3Service.uploadFile(imageMapper.convertFilePermissions(file, MIN_IMAGE_PIC)));

        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);

        return UserProfilePicDto.fromUserProfilePic(userProfilePic);
    }

    public InputStream getUserPic(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));

        if (user.getUserProfilePic() == null) {
            throw new EntityNotFoundException("User " + userId + " not found");
        }
        return s3Service.downloadFile(user.getUserProfilePic().getFileId());
    }

    public void deleteUserPic(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User " + userId + " not found"));

        s3Service.deleteFile(user.getUserProfilePic().getFileId());
        s3Service.deleteFile(user.getUserProfilePic().getSmallFileId());

        user.getUserProfilePic().setFileId(null);
        user.getUserProfilePic().setSmallFileId(null);

        userRepository.save(user);
    }

    public UserDto getUserProfile(ProfileViewEventDto profileViewEventDto) {
        if (profileViewEventDto.getViewerId() == profileViewEventDto.getAuthorId()) {
            throw new IllegalArgumentException("coincidence of the id of the author and the viewer");
        }

        UserDto userDto = findUserById(profileViewEventDto.getAuthorId());
        profileViewMessagePublisher.publish(profileViewEventDto);

        return userDto;
    }
}
