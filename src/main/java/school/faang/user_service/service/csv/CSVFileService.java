package school.faang.user_service.service.csv;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.person.PersonMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CSVFileService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int BATCH_SIZE = 100;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final CsvMapper csvMapper;
    private final PersonMapper personMapper = PersonMapper.INSTANCE;
    private final JdbcTemplate jdbcTemplate;

//    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Qualifier("threadPoolForConvertCsvFile")
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Transactional
    public void convertCsvFile(List<Person> persons) {
        ExecutorService executor = threadPoolTaskExecutor.getThreadPoolExecutor();
        try {
            List<CompletableFuture<Void>> futures = persons.stream().map(person -> {
                try {
                    validateIsUnique(person.getFirstName() + "_" + person.getLastName(), "username");
                    validateIsUnique(person.getContactInfo().getPhone(), "phone");
                    validateIsUnique(person.getContactInfo().getEmail(), "email");
                    User user = personMapper.toEntity(person);
                    return CompletableFuture.runAsync(() -> setUpBeforeSaveToDB(user), executor);
                } catch (RejectedExecutionException e) {
                    throw new RuntimeException("Task rejected due to executor termination: " + e.getMessage(), e);
                }
            }).toList();

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException("Processing timed out", e);
        } finally {
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
    }

    private void validateIsUnique(String valueToCheck, String column) {
        String query = String.format("SELECT COUNT(*) = 0 FROM users WHERE %s = ?", column);
        boolean isUnique = jdbcTemplate.queryForObject(query, Boolean.class, valueToCheck);
        if (!isUnique) {
            throw new DataValidationException(column + " is not unique, someone has already registered " + valueToCheck);
        }
    }

    private void setUpBeforeSaveToDB(User user) {
        user.setPassword(generatePassword());
        assignCountryToUser(user);
        saveUser(user);
    }

    private void assignCountryToUser(User user) {
        String countryName = user.getCountry().getTitle();
        Country country = countryRepository.findByName(countryName)
                .orElseGet(() -> countryRepository.save(Country.builder()
                        .title(countryName)
                        .residents(Collections.singletonList(user))
                        .build()));
        user.setCountry(country);
    }

    private String generatePassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);

        StringBuilder combinedChars = new StringBuilder()
                .append(upperCaseLetters)
                .append(lowerCaseLetters)
                .append(numbers)
                .append(specialChar)
                .append(totalChars);

        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());

        Collections.shuffle(pwdChars, RANDOM);

        return pwdChars.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    private synchronized void saveUser(User user){
        userRepository.save(user);
    }
}