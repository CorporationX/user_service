package school.faang.user_service.service.user.upload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.common.PasswordCipher;
import school.faang.user_service.common.PasswordGenerator;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserUploadServiceImpl implements UserUploadService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordGenerator passwordGenerator;
    private final PasswordCipher passwordCipher;

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Map<String, Country>> getAllCountries() {
        Map<String, Country> countries = countryRepository.findAll().stream()
                .collect(Collectors.toMap(Country::getTitle, country -> country));
        log.info("Found {} countries", countries.size());
        return CompletableFuture.completedFuture(countries);
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<User>> setCountriesToUsers(List<User> users, Map<String, Country> countries) {
        Set<Country> unsavedCountries = new HashSet<>();
        users.forEach(user -> {
            String countryTitle = user.getCountry().getTitle();
            if (countries.containsKey(countryTitle)) {
                user.setCountry(countries.get(countryTitle));
            } else {
                unsavedCountries.add(Country.builder().title(countryTitle).build());
            }
        });
        List<Country> savedCountries = countryRepository.saveAll(unsavedCountries);
        log.info("{} countries saved", savedCountries.size());
        savedCountries.forEach(country -> countries.put(country.getTitle(), country));
        users.stream()
                .filter(user -> user.getCountry().getId() == 0)
                .forEach(user -> user.setCountry(countries.get(user.getCountry().getTitle())));
        return CompletableFuture.completedFuture(users);
    }

    @Async("taskExecutor")
    @Override
    public void saveUsers(List<User> users) {
        users.forEach(user -> user.setPassword(
                passwordCipher.encryptPassword(passwordGenerator.generatePassword(user.getUsername()))));
        log.info("{} users to save", users.size());
        userRepository.batchInsertUsers(users);
    }
}
