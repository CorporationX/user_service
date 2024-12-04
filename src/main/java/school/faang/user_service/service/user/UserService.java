package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user_jira.UserJiraCreateUpdateDto;
import school.faang.user_service.dto.user_jira.UserJiraDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.userJira.UserJira;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.mapper.user_jira.UserJiraMapper;
import school.faang.user_service.pojo.user.Person;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user_jira.UserJiraService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final UserJiraMapper userJiraMapper;
    private final UserJiraService userJiraService;

    private final CountryService countryService;
    private static final String FILE_TYPE = "text/csv";

    @Transactional(readOnly = true)
    public UserDto getUser(long userId) {
        return userMapper.toDto(findUserById(userId));
    }

    @Transactional(readOnly = true)
    public User getUserEntity(long userId) {
        return findUserById(userId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids) {
        return userRepository.findAllById(ids).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<Long> getNotExistingUserIds(List<Long> userIds) {
        return userIds.isEmpty() ? Collections.emptyList() : userRepository.findNotExistingUserIds(userIds);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getNotPremiumUsers(UserFilterDto filterDto) {
        Stream<User> usersToFilter = userRepository.findAll().stream();
        Stream<User> notPremiumUsers = filterPremiumUsers(usersToFilter);

        List<UserDto> filteredUsers = filter(notPremiumUsers, filterDto);
        log.info("Got {} filtered users, by filter {}", filteredUsers.size(), filterDto);
        return filteredUsers;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getPremiumUsers(UserFilterDto filterDto) {
        Stream<User> users = userRepository.findPremiumUsers();

        List<UserDto> filteredUsers = filter(users, filterDto);
        log.info("Got {} filtered premium users, by filter {}", filteredUsers.size(), filterDto);
        return filteredUsers;
    }

    @Transactional
    public UserJiraDto saveOrUpdateUserJiraInfo(long userId, String jiraDomain, UserJiraCreateUpdateDto createUpdateDto) {
        log.info("Request received to save or update user (ID {}) Jira account information for Jira domain {}", userId, jiraDomain);

        User user = findUserById(userId);
        UserJira userJira = userJiraMapper.toEntity(createUpdateDto);
        userJira.setUser(user);
        userJira.setJiraDomain(jiraDomain);
        UserJira savedUserJira = userJiraService.saveOrUpdate(userJira);

        log.info("Request to save or update user (ID {}) Jira account information for Jira domain {} processed successfully",
                userId, jiraDomain
        );
        return userJiraMapper.toDto(savedUserJira);
    }

    @Transactional(readOnly = true)
    public UserJiraDto getUserJiraInfo(long userId, String jiraDomain) {
        log.info("Received request to get user (ID {}) Jira account information for Jira domain {}", userId, jiraDomain);
        UserJira userJira = userJiraService.getByUserIdAndJiraDomain(userId, jiraDomain);
        log.info("Found user (ID {}) Jira account information for Jira domain {}", userId, jiraDomain);
        return userJiraMapper.toDto(userJira);
    }

    public void banUser(Long userId) {
        User user = findUserById(userId);
        user.setBanned(true);
        log.info("User {} is banned", userId);
        userRepository.save(user);
    }

    public void handleUserBanMessage(Long authorId) {
        User user = findUserById(authorId);
        user.setBanned(true);
        log.info("User {} is banned", authorId);
        userRepository.save(user);
    }

    private User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND, userId)));
    }

    @Transactional
    public List<UserDto> parsePersonDataIntoUserDto(MultipartFile csvFile) {
        if (csvFile.isEmpty() || !Objects.equals(csvFile.getContentType(), FILE_TYPE)) {
            throw new IllegalArgumentException("Invalid file type or there is no file." +
                    " Please upload a CSV file.");
        }
        try {
            InputStream inputStream = csvFile.getInputStream();
            CsvMapper csvMapper = new CsvMapper();
            csvMapper.findAndRegisterModules();

            CsvSchema schema = csvMapper.schemaFor(Person.class).withHeader();
            MappingIterator<Person> iterator = csvMapper.readerFor(Person.class).with(schema).readValues(inputStream);
            List<Person> persons = iterator.readAll();
            log.info("CSV file processed. Number of records: {}", persons.size());

            return saveUsers(persons);
        } catch (IOException e) {
            log.error("Error processing CSV file", e);
            throw new RuntimeException("Error processing CSV file", e);
        }
    }

    private List<UserDto> saveUsers(List<Person> persons) {
        log.info("Starting to save {} users", persons.size());

        List<CompletableFuture<User>> futures = persons.stream()
                .map(this::convertToUser)
                .toList();

        List<User> users = futures.stream()
                .map(future -> {
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException("Error processing user", e);
                    }
                })
                .collect(Collectors.toList());

        List<User> savedUsers = userRepository.saveAll(users);
        log.info("Successfully saved {} users to the database", savedUsers.size());

        return savedUsers.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Async("taskExecutor")
    protected CompletableFuture<User> convertToUser(Person person) {
        log.debug("Processing person: {}", person);
        User user = userMapper.toUser(person);
        user.setPassword(generatePassword());

        Country country = countryService.getOrCreateCountry(person.getCountry());
        user.setCountry(country);

        return CompletableFuture.completedFuture(user);
    }

    private String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private List<UserDto> filter(Stream<User> usersStream, UserFilterDto filterDto) {
        return userMapper.entityStreamToDtoList(userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filterDto))
                .reduce(usersStream,
                        (users, userFilter) -> userFilter.apply(users, filterDto),
                        (a, b) -> b));
    }

    private Stream<User> filterPremiumUsers(Stream<User> users) {
        return users.filter(user -> user.getPremium() == null
                || user.getPremium().getEndDate() == null
                || user.getPremium().getEndDate().isBefore(LocalDateTime.now()));
    }

}
