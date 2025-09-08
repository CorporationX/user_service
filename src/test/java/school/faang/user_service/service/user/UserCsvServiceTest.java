package school.faang.user_service.service.user;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.person.Person;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.user.CountryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCsvServiceTest {
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CsvMapper csvMapper;
    @InjectMocks
    public UserCsvServiceImpl userCsvService;

    private static final long COUNTRY_ID = 1L;
    private static final String COUNTRY = "USA";
    private static final Country COUNTRY_CLASS = new Country(COUNTRY_ID, COUNTRY, null);
    private static final String CITY = "Davis";
    private static final String FIRST_NAME = "Vlad";
    private static final String LAST_NAME = "Nikitin";
    private static final String USER_NAME = "Vlad Nikitin";
    private static final String EMAIL = "email";
    private static final String PHONE = "89099909090";
    private static final String PASSWORD = "fvEWGEJk";
    private static final String STATE = "California";
    private static final String FACULTY = "economic";
    private static final String YEAR_OF_STUDY = "2024";
    private static final String MAJOR = "manager";
    private static final String EMPLOYER = "Amazon";
    private static final String ABOUT_ME = "My state is " + STATE + ", " + "I study at the Faculty of " + FACULTY
            + ", I entered in " + YEAR_OF_STUDY + ", my specialty is " + MAJOR + ", I work at " + EMPLOYER;

    @Test
    @DisplayName("Should get the user out person")
    public void testConvertPersonsToUser() {
        Person person = createPerson();

        when(countryRepository.findByTitle(COUNTRY)).thenReturn(Optional.of(COUNTRY_CLASS));

        User user = userCsvService.convertPersonToUser(person);
        user.setPassword(PASSWORD);

        User result = createUser();

        assertEquals(user, result);
    }

    private User createUser() {
        return User.builder()
                .username(USER_NAME)
                .email(EMAIL)
                .phone(PHONE)
                .password(PASSWORD)
                .aboutMe(ABOUT_ME)
                .country(createCountry())
                .city(CITY)
                .build();
    }

    private Person createPerson() {
        Person person = new Person();
        person.setFirstName(FIRST_NAME);
        person.setLastName(LAST_NAME);
        person.setCountry(COUNTRY);
        person.setEmail(EMAIL);
        person.setPhone(PHONE);
        person.setCity(CITY);
        person.setState(STATE);
        person.setFaculty(FACULTY);
        person.setYearOfStudy(YEAR_OF_STUDY);
        person.setMajor(MAJOR);
        person.setEmployer(EMPLOYER);
        return person;
    }

    private Country createCountry() {
        return Country.builder()
                .id(COUNTRY_ID)
                .title(COUNTRY)
                .build();
    }
}
