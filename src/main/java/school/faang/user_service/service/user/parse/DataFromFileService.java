package school.faang.user_service.service.user.parse;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static school.faang.user_service.service.user.util.PasswordGenerator.generate;

/**
 * Класс для преобразования InputStream в List<UserDto>.
 * InputStream методом toCsvPartDivider и объектом ReadAndDivide преобразуется в List<CsvPart>.
 * List<CsvPart>, методом convertToInputStreamList преобразуется в List<InputStream>.
 * List<InputStream> методом multiParseCsv и объектом CsvParser преобразуются в List<Person>.
 * List<Person> методом convertToUserList преобразуются в List<User>.
 * List<User> методом filter фильтруются, отсеиваются те пользователи, которые есть в базе данных,
 * После этого объекты User сохраняются в базу данных, проеобразуются в DTO и возвращаются.
 */
@Service
@AllArgsConstructor
public class DataFromFileService {

    private UserRepository userRepository;

    private CountryRepository countryRepository;

    private UserMapper userMapper;

    private CsvParser csvParser;

    private ReadAndDivide readAndDivide;

    public List<UserDto> saveUsersFromFile(InputStream inputStream) {
        List<CsvPart> csvParts = readAndDivide.toCsvPartDivider(inputStream);
        List<InputStream> inputStreamParts = convertToInputStreamList(csvParts);
        List<Person> allPersons = csvParser.multiParseCsv(inputStreamParts);
        List<User> allUsers = convertToUserList(allPersons);
        List<User> updatedUsers = filter(allUsers);

        userRepository.saveAll(updatedUsers);
        return userMapper.toDto(updatedUsers);
    }

    private List<InputStream> convertToInputStreamList(List<CsvPart> parts) {
        return parts.stream()
                .map(part -> String.join(System.lineSeparator(), part.getLines()))
                .map(s -> new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)))
                .collect(Collectors.toList());
    }

    private List<User> convertToUserList(List<Person> persons) {
        List<User> users = new ArrayList<>();
        Map<String, Country> countryMap = StreamSupport.stream(countryRepository.findAll().spliterator(), false)
                .collect(Collectors.toMap(Country::getTitle, Function.identity()));

        for (Person person : persons) {
            String countryName = person.getContactInfo().getAddress().getCountry();
            User user = userMapper.toUser(person);
            user.setPassword(generate());

            Country country = countryMap.get(countryName);
            if (country != null) {
                user.setCountry(country);
            } else {
                country = new Country();
                country.setTitle(countryName);
                countryRepository.save(country);
                user.setCountry(country);
                countryMap.put(countryName, country);
            }
            users.add(user);
        }
        return users;
    }

    private List<User> filter(List<User> allUsers) {
        Set<String> existingUsernames = new HashSet<>();
        Set<String> existingEmails = new HashSet<>();
        Set<String> existingPhones = new HashSet<>();

        userRepository.findAll().forEach(user -> {
            existingUsernames.add(user.getUsername());
            existingEmails.add(user.getEmail());
            existingPhones.add(user.getPhone());
        });

        return allUsers.stream()
                .filter(user -> !existingUsernames.contains(user.getUsername()) &&
                        !existingEmails.contains(user.getEmail()) &&
                        !existingPhones.contains(user.getPhone()))
                .collect(Collectors.toList());
    }
}
