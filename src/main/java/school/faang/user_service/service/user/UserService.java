package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.PersonSchemaForUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FileException;
import school.faang.user_service.mapper.CountryMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.amazon.AvatarService;
import school.faang.user_service.validator.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AvatarService avatarService;
    private final UserValidator userValidator;
    private final CsvMapper csvMapper;
    private final CsvSchema schema;
    private final CountryService countryService;
    private final CountryMapper countryMapper;

    @Value("${services.dice-bear.url}")
    private String URL;
    @Value("${services.dice-bear.size}")
    private String SIZE;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        addCreateData(user);

        synchronized (userRepository) {
            user = userRepository.save(user);
        }

        return userMapper.toDto(user);
    }

    public List<UserDto> createUserCSV(InputStream inputStream) {
        List<PersonSchemaForUser> persons = null;
        try {
            persons = parseCsv(inputStream);
        } catch (IOException e) {
            throw new FileException("File exception " + e);
        }

        List<CompletableFuture<UserDto>> futures = makeFutureList(persons);
        return completeFutureList(futures);
    }

    public User findUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataValidationException("User was not found"));
    }

    public boolean areOwnedSkills(long userId, List<Long> skillIds) {
        if (skillIds.isEmpty()) {
            return true;
        }
        return userRepository.countOwnedSkills(userId, skillIds) == skillIds.size();
    }

    public UserDto getUser(long id) {
        return userMapper.toDto(findUserById(id));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        List<User> users = StreamSupport.stream(userRepository.findAllById(ids).spliterator(), false).toList();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    private List<PersonSchemaForUser> parseCsv(InputStream inputStream) throws IOException {
        MappingIterator<PersonSchemaForUser> iterator = csvMapper.readerFor(PersonSchemaForUser.class)
                .with(schema)
                .readValues(inputStream);
        return iterator.readAll();
    }

    private List<CompletableFuture<UserDto>> makeFutureList(List<PersonSchemaForUser> persons) {
        return persons.stream()
                .map(person -> CompletableFuture.supplyAsync(() -> {
                    try {
                        UserDto userDto = userMapper.personToUserDto(person);
                        userDto.setPassword(ThreadLocalRandom.current().nextInt() + "");
                        userValidator.validateUserDto(userDto);
                        createUser(userDto);
                        // SMS to user.getEmail with - "You password for CorporationX is:"+user.getPassword()
                        return userDto;
                    } catch (Exception e) {
                        return UserDto.builder()
                                .username(person.getUsername())
                                .aboutMe("Didn't save " + e.getMessage())
                                .build();
                    }
                }))
                .toList();
    }

    private List<UserDto> completeFutureList(List<CompletableFuture<UserDto>> futures) {
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(dto -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .join();
    }

    private void addCreateData(User user) {
        user.setCountry(countryMapper.toCountry(countryService.findCountryByTitle(user.getCountry().getTitle())));
        UserProfilePic userProfilePic = UserProfilePic.builder()
                .name(user.getUsername() + ThreadLocalRandom.current().nextInt())
                .build();

        createDiceBearAvatar(userProfilePic);
        user.setUserProfilePic(userProfilePic);
    }

    private void createDiceBearAvatar(UserProfilePic userProfilePic) {
        userProfilePic.setFileId(URL + userProfilePic.getName());
        userProfilePic.setSmallFileId(URL + userProfilePic.getName() + SIZE);
        avatarService.saveToAmazonS3(userProfilePic);
    }
}