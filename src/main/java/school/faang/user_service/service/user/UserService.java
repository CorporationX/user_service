package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.password.PasswordService;
import school.faang.user_service.validator.user.UserValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CountryService countryService;
    private final PasswordService passwordService;
    private final UserValidator userValidator;
    @Qualifier("taskExecutor")
    private final Executor taskExecutor;

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));
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
}