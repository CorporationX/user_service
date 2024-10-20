package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.apache.batik.transcoder.TranscoderException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.mentorship.MentorshipService;
import school.faang.user_service.service.avatar.AvatarService;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validator.user.UserValidator;
import school.faang.user_service.service.password.PasswordService;
import school.faang.user_service.dto.user.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.StreamSupport;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final GoalRepository goalRepository;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final AvatarService avatarService;
    private final CountryService countryService;
    private final S3Service s3Service;
    private final PasswordService passwordService;
    @Qualifier("taskExecutor")
    private final Executor taskExecutor;



    public UserDto deactivate(Long userId) {
        User user = userRepository.findById(userId).get();
        removeEvents(userId);
        removeGoals(userId);
        user.setActive(false);
        return userMapper.toDto(userRepository.save(user));
    }

    public void removeMenteeAndGoals(Long userId) {
        mentorshipService.removeMenteeGoals(userId);
        mentorshipService.removeMenteeFromUser(userId);
    }

    @Scheduled(cron = "@daily")
    public void deleteInactiveUsers() {
        StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(user -> !user.isActive()).filter(user ->user.getUpdatedAt().plusDays(90).isBefore(LocalDateTime.now()))
                .forEach(userRepository::delete);
    }

    private void removeGoals(Long userId) {
        userValidator.validateThatUserIdExist(userId);
        User user = userRepository.findById(userId).get();
        if (!user.getGoals().isEmpty()) {
            user.getGoals().forEach(goal -> {
                goal.getUsers().remove(user);
                if (goal.getUsers().isEmpty()) {
                    goalRepository.delete(goal);
                }
            });
        }
    }

    private void removeEvents(Long userId) {
        userValidator.validateThatUserIdExist(userId);
        User user = userRepository.findById(userId).get();
        if (!user.getOwnedEvents().isEmpty()) {
            user.getOwnedEvents().forEach(event -> {
                event.setStatus(EventStatus.CANCELED);
                eventRepository.save(event);
            });
        }
    }

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    @Transactional
    public UserDto createUser(UserDto userDto) throws IOException, TranscoderException {
        userValidator.uniqueUsername(userDto.getUsername());
        userValidator.uniqueEmail(userDto.getEmail());
        userValidator.uniquePhone(userDto.getPhone());

        User user = userMapper.toEntity(userDto);
        user.setCountry(countryService.getCountryOrCreate(user.getCountry().getTitle()));

        userRepository.save(user);

        s3Service.uploadAvatar(user.getId(), avatarService.downloadSvgAsMultipartFile(avatarService.getRandomAvatarUrl()));

        return userMapper.toDto(user);
    }

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<Void> processCSVAsync(InputStream file) throws IOException {
        List<Person> persons = readPersonsFromCSV(file);

        List<CompletableFuture<Void>> futures = persons.stream()
                .map(person -> CompletableFuture.runAsync(() -> {
                    if (!userValidator.findUserByEmail(person.getContactInfo().getEmail()) &&
                            !userValidator.findUserByPhone(person.getContactInfo().getPhone())) {
                        processPerson(person);
                    } else {
                        log.info("User with email {} or phone {} already exists, skipping...",
                                person.getContactInfo().getEmail(), person.getContactInfo().getPhone());
                    }
                }, taskExecutor))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allOf;
    }

    private List<Person> readPersonsFromCSV(InputStream file) throws IOException {
        ObjectReader csvOpenReader = new CsvMapper()
                .readerFor(Person.class)
                .with(CsvSchema.emptySchema().withHeader());
        MappingIterator<Person> mappingIterator = csvOpenReader.readValues(file);
        return mappingIterator.readAll();
    }

    private void processPerson(Person person) {
        User user = userMapper.personToUser(person);
        user.setPassword(passwordService.generatePassword());
        Country country = countryService.getCountryOrCreate(person.getContactInfo().getAddress().getCountry());
        user.setCountry(country);
        userRepository.save(user);
        log.info("Processed and saved user: {}", user.getEmail());
    }

    public List<Long> findAllActiveUserIds() {
        return userRepository.findAllActiveUserIds();
    }
}
