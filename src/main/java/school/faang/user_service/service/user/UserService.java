package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.password.PasswordService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CountryService countryService;
    private final PasswordService passwordService;

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    @Transactional
    @Async
    public CompletableFuture<Void> processCSVAsync(InputStream file) throws IOException {
        List<Person> persons = readPersonsFromCSV(file);

        List<CompletableFuture<Void>> futures = persons.stream()
                .map(person -> CompletableFuture.runAsync(() -> processPerson(person)))
                .collect(Collectors.toList());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allOf;
    }

    private List<Person> readPersonsFromCSV(InputStream file) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = csvMapper.schemaFor(Person.class).withHeader();
        MappingIterator<Person> iterator = csvMapper.readerFor(Person.class)
                .with(schema)
                .readValues(file);

        return iterator.readAll();
    }

    private void processPerson(Person person) {
        User user = userMapper.personToUser(person);
        user.setPassword(passwordService.generatePassword());
        Country country = countryService.getCountryOrCreate(person.getContactInfo().getAddress().getCountry());
        user.setCountry(country);
        userRepository.save(user);
    }
}