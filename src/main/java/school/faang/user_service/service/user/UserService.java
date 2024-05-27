package school.faang.user_service.service.user;

import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.cache.HashMapCountry;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.threadPool.ThreadPoolForConvertCsvFile;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final ThreadPoolForConvertCsvFile threadPoolForConvertCsvFile;
    private final HashMapCountry hashMapCountry;

    @Transactional
    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> users = userRepository.findPremiumUsers();

        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .flatMap(userFilter -> userFilter.apply(users, userFilterDto))
                .map(userMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public User getById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("Пользователь с id " + userId + " не найден"));
    }

    @Transactional()
    public void convertScvFile(List<Person> persons) {
        ExecutorService executor = threadPoolForConvertCsvFile.taskExecutor();
        List<CompletableFuture<Void>> futures = persons.stream().map(person -> {
                    System.out.println("persons = " + person);
                    User user = userMapper.toEntity(person);
                    System.out.println("user = " + user);
                    return CompletableFuture.runAsync(() -> setUpBeforeSaveToDB(user), executor);
                }).toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allOf.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            executor.shutdownNow();
            throw new RuntimeException(e);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void setUpBeforeSaveToDB(User user) {
        user.setPassword("1");

        // Создать мапу для выгрузки пользователей из бы, а потом простого обращения к ней.
        if (hashMapCountry.isContainsKey(user.getCountry().getTitle())) {
            setCountryForUser(user);
        } else {
            saveCountry(user.getCountry(), user);
        }
    }

    private void saveCountry(Country country, User user) {
        Country countrySaved = countryRepository.save(country);
        CountryDto countryDto = new CountryDto();
        countryDto.setId(countrySaved.getId());
        countryDto.setTitle(countrySaved.getTitle());
        hashMapCountry.addCountry(countryDto);
        setCountryForUser(user);
    }

    // Присвоить id страны, так как сейчас мы получаем только название а в бд явно храниться уникальное значение.
    private void setCountryForUser(User user) {
        CountryDto country = hashMapCountry.findCountryByTitle(user.getCountry().getTitle());
        Country countryS = new Country();
        countryS.setId(country.getId());
        countryS.setTitle(country.getTitle());
        user.setCountry(countryS);
        saveUser(user);
    }

    private synchronized void saveUser(User user) {
        userRepository.save(user);
    }
}