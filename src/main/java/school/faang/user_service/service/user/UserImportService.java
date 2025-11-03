package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserImportMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.CountryRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserImportService {

    private final CsvMapper csvMapper;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public List<UserDto> uploadUsersCsv(MultipartFile file) throws IOException {
        List<Person> people = parseCsvToPersons(file);
        List<User> users = convertToUsers(people);
        users = userRepository.saveAll(users);
        return users.stream().map(userMapper::toUserDto).toList();
    }

    private List<Person> parseCsvToPersons(MultipartFile file) throws IOException {
        CsvSchema schema = csvMapper.schemaFor(Person.class)
                .withHeader()
                .withColumnReordering(true);

        try (InputStream inputStream = file.getInputStream()) {
            MappingIterator<Person> personIterator = csvMapper
                    .readerFor(Person.class)
                    .with(schema)
                    .readValues(inputStream);

            return personIterator.readAll();
        }
    }

    private List<User> convertToUsers(List<Person> persons) {
        return persons.stream()
                .map(p -> {
                    Optional<User> existingUser = userRepository.findByPhoneOrEmail(p.getPhone(), p.getEmail());
                    if (existingUser.isPresent()) {
                        throw new DataValidationException("Email %s or phone %s is already existing, use unique email"
                                .formatted(p.getEmail(), p.getPhone()));
                    }

                    Optional<Country> countryOptional = countryRepository.findByTitle(p.getCountry());
                    Country country = countryOptional.orElseGet(() -> countryRepository.save(Country.builder()
                            .title(p.getCountry())
                            .build()));
                    return UserImportMapper.toUser(p, country);
                })
                .toList();
    }
}
