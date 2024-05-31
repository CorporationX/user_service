package school.faang.user_service.service.user;

import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.cache.HashMapCountry;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.generator.password.UserPasswordGenerator;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.threadPool.ThreadPoolForConvertCsvFile;
import school.faang.user_service.validator.UserValidator;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final PersonMapper personMapper;
    private final ThreadPoolForConvertCsvFile threadPoolForConvertCsvFile;
    private final UserPasswordGenerator userPasswordGenerator;
    private final HashMapCountry hashMapCountry;
    private final CountryService countryService;
    private final UserValidator userValidator;

    @Transactional(readOnly = true)
    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->{
                    log.debug("The user with id not found. id: {}", userId);
                    return new DataValidationException("Пользователь с id " + userId + " не найден");
                });
    }

    @Transactional
    public void saveUser(User user) {
        userValidator.validateUserNotExists(user);
        log.debug("Saved user to db: {}", user);
        userRepository.save(user);
    }

    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> users = userRepository.findPremiumUsers();
        log.debug("Received users from db: {}", users);

        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(users, userFilterDto))
                .map(userMapper::toDto).toList();
    }

    @Transactional
    public void convertCsvFile(List<Person> persons) {
        ExecutorService executor = threadPoolForConvertCsvFile.taskExecutor();
        List<CompletableFuture<Void>> futures = persons.stream()
                .map(person -> {
                    User user = personMapper.toEntity(person);
                    log.debug("Received user from Csv file. User: {}", user);
                    return CompletableFuture.runAsync(() -> setUpBeforeSaveToDB(user), executor);
                })
                .toList();


        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("An error occurred while waiting for completion of tasks", e);
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            executor.shutdownNow();
            log.error("Timeout occurred while waiting for completion of tasks", e);
            throw new RuntimeException(e);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            log.error("An error occurred while shutting down executor", e);
            Thread.currentThread().interrupt();
        }
    }

    private void setUpBeforeSaveToDB(User user) {
        user.setPassword(userPasswordGenerator.createPassword());

        User readyUser;
        if (hashMapCountry.isContainsKey(user.getCountry().getTitle())) {
            readyUser = countryService.setCountryForUser(user);
        } else {
           readyUser = countryService.saveCountry(user.getCountry(), user);
        }
        saveUser(readyUser);
    }
}