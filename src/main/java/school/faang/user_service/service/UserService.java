package school.faang.user_service.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.MessageError;
import school.faang.user_service.mapper.PersonMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final UserMapper userMapper;
    private final PersonMapper personMapper;

    public List<User> getAllUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds)
                .stream()
                .toList();
    }

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(MessageError.USER_DOES_NOT_EXIST));
        return userMapper.toDto(user);
    }

    public List<UserDto> saveUsers(InputStream inputStream) throws IOException {

        FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/files/students-copy.csv");
        fileOutputStream.write(inputStream.readAllBytes());

        inputStream.close();
        fileOutputStream.close();

        Reader myReader = new FileReader("students-copy.csv");
        CsvMapper mapper = new CsvMapper();

        //mapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
        //mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
        //mapper.disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',');
        MappingIterator<Person> personMappingIterator = mapper
                .readerFor(Person.class)
                .with(schema)
                .readValues(myReader);

        new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .writeValue(new File("src/main/resources/json/person.json"), personMappingIterator.readAll());

        List<Person> persons = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(new File("src/main/resources/json/person.json"),
                        new TypeReference<List<Person>>() {
                        });
        System.out.println(persons);

        List<User> studentsToUsers = new ArrayList<>();
        //List<Country> countries = countryRepository.findAll();
        //List<String> countryTitles = new ArrayList<>();
        //countries.forEach(country -> countryTitles.add(country.getTitle()));
        //System.out.println(countryTitles);

        persons.forEach(person -> {
            User studentToUser = personMapper.toUser(person);
            //System.out.println(studentToUser.getUsername());
            boolean existsByUsernameResult = userRepository.existsByUsername(studentToUser.getUsername());
            boolean existsByEmailResult = userRepository.existsByEmail(studentToUser.getEmail());
            boolean existsByPhoneResult = userRepository.existsByPhone(studentToUser.getPhone());
            validateUserBeforeSave(studentToUser, existsByUsernameResult, existsByEmailResult,existsByPhoneResult);
            if(!existsByUsernameResult && !existsByEmailResult && !existsByPhoneResult) {
                studentToUser.setPassword(generatePassword());
                //System.out.println(generatePassword());
                Country userCountry = studentToUser.getCountry();
                //System.out.println("userCountry:" + userCountry);

                if (userCountry != null) {
                    String userCountryTitle = userCountry.getTitle();


//                    if (!countryTitles.contains(userCountryTitle)) {
//                        System.out.println(userCountryTitle);
//                        Country saveCountry = countryRepository.save(userCountry);
//                        System.out.println("saveCountry: " + saveCountry);
//                        studentToUser.setCountry(saveCountry);
//                        System.out.println("setCountry: " + studentToUser.getCountry());
//                        countryTitles.add(saveCountry.getTitle());
//                    }
//
//                    List<Country> equalsCountries = countryRepository.findAll().stream().filter(
//                            country -> country.getTitle().equals(userCountryTitle)).limit(1).toList();
//                    System.out.println("equalsCountries" + equalsCountries);
//                    Country equalsCountry = equalsCountries.get(0);
//                    System.out.println("equalsCountry: " + equalsCountry);
//
//                    studentToUser.setCountry(equalsCountry);
//                    System.out.println("assignedCountry: " + studentToUser.getCountry());

                    boolean existsCountryByTitleResult = countryRepository.existsCountryByTitle(userCountryTitle);
                    //System.out.println(!existsCountryByTitleResult);
                    if (!existsCountryByTitleResult) {
                        Country saveCountry = countryRepository.save(userCountry);
                        //System.out.println("saveCountry: " + saveCountry);
                        studentToUser.setCountry(saveCountry);
                        //System.out.println("setCountry: " + studentToUser.getCountry());
                        //countryTitles.add(saveCountry.getTitle());
                    }
                    List<Country> equalsCountries = countryRepository.findAll().stream().filter(
                            country -> country.getTitle().equals(userCountryTitle)).limit(1).toList();
                    //System.out.println("equalsCountries" + equalsCountries);
                    Country equalsCountry = equalsCountries.get(0);
                    //System.out.println("equalsCountry: " + equalsCountry);

                    studentToUser.setCountry(equalsCountry);
                    //System.out.println("assignedCountry: " + studentToUser.getCountry());

                }
                userRepository.save(studentToUser);
            }
            studentsToUsers.add(studentToUser);
        });

        //System.out.println("studentsToUsers" + studentsToUsers);
        return userMapper.toDtoList(studentsToUsers);
    }

    private void validateUserBeforeSave(User studentToUser,
                                        boolean existsByUsernameResult,
                                        boolean existsByEmailResult,
                                        boolean existsByPhoneResult) {

        if (existsByUsernameResult) {
            log.warn("User with username {} already exists", studentToUser.getUsername());
            //throw new DataValidationException("User with username " + studentToUser.getUsername() + " already exists");
        }  else if (existsByEmailResult) {
            log.warn("User with email {} already exists", studentToUser.getEmail());
            //throw new DataValidationException("User with email " + studentToUser.getEmail() + " already exists");
        } else if (existsByPhoneResult) {
            log.warn("User with phone number {} already exists", studentToUser.getPhone());
            //throw new DataValidationException("User with phone number " + studentToUser.getPhone() + " already exists");
        }
    }

    private String generatePassword() {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@$%^&*";
        StringBuilder pass = new StringBuilder(10);
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            pass.append(symbols.charAt(rnd.nextInt(symbols.length())));
        }
        return pass.toString();
    }
}
