package school.faang.user_service.service.user;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.user.CountryRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCsvServiceImpl implements UserCsvService {
    private final CountryRepository countryRepository;
    private final CsvMapper mapper;

    public List<Person> readPersonsFromCsv(InputStream fileStream) throws IOException {
        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        return mapper.readerFor(Person.class)
                .with(schema)
                .<Person>readValues(fileStream)
                .readAll();
    }

    public List<User> convertPersonsToUsers(List<Person> persons) {
        return persons.parallelStream()
                .map(this::convertPersonToUser)
                .collect(Collectors.toList());
    }

    public User convertPersonToUser(Person person) {
        RandomPassword passwordGenerator = new RandomPassword();
        String userName = createUserName(person.getFirstName(), person.getLastName());
        String password = passwordGenerator.nextString(userName.hashCode());
        String aboutMe = createAboutMe(person.getState(), person.getFaculty(),
                person.getYearOfStudy(), person.getMajor(), person.getEmployer());
        Country country = findCountry(person.getCountry());
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        user.setEmail(person.getEmail());
        user.setPhone(person.getPhone());
        user.setCountry(country);
        user.setCity(person.getCity());
        user.setAboutMe(aboutMe);
        return user;
    }

    private String createUserName(String first, String second) {
        return first + " " + second;
    }

    private String createAboutMe(String state, String faculty, String yearOfStudy, String major, String employer) {
        String result = "I study at the Faculty of " + faculty + ", I entered in "
                + yearOfStudy + ", my specialty is " + major;
        if (state != null) {
            result = "My state is " + state + ", " + result;
        }
        if (employer != null) {
            result = result + ", I work at " + employer;
        }
        return result;
    }

    private Country findCountry(String countryTitle) {
        return countryRepository.findByTitle(countryTitle)
                .orElseGet(() -> {
                    Country newCountry = Country.builder()
                            .title(countryTitle)
                            .build();
                    return countryRepository.save(newCountry);
                });
    }

    public class RandomPassword {
        public static final String enUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String enLower = enUpper.toLowerCase();
        public static final String digits = "0123456789";
        public static final String symbols = enUpper + enLower + digits;

        public String nextString(Integer length) {
            Random random = new Random();
            char[] buf = new char[8];
            for (int idx = 0; idx < buf.length; ++idx) {
                buf[idx] = symbols.charAt(random.nextInt(symbols.length()));
            }
            return new String(buf);
        }
    }
}

