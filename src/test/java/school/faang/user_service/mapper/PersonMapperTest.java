package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.pojo.student.Address;
import school.faang.user_service.pojo.student.ContactInfo;
import school.faang.user_service.pojo.student.Education;
import school.faang.user_service.pojo.student.Person;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PersonMapperTest {

    @Spy
    private PersonMapperImpl personMapper;
    private Person person;
    private User user;

    @BeforeEach
    void setUp() {
        person = Person.builder()
                .firstName("firstName")
                .lastName("lastName")
                .contactInfo(ContactInfo.builder()
                        .email("email")
                        .phone("phone")
                        .address(Address.builder()
                                .city("city")
                                .state("state")
                                .country("country")
                                .build()
                                ).build())
                .education(Education.builder()
                        .faculty("faculty")
                        .yearOfStudy(2020)
                        .major("major")
                        .build()
                )
                .employer("employer")
                .build();

        user = User.builder()
                .username("firstName_lastName")
                .email("email")
                .phone("phone")
                .city("city")
                .country(Country.builder()
                        .title("country")
                        .build())
                .aboutMe("about me: state - state, faculty - faculty, year of study - 2020, major - major, employer - employer")
                .build();
    }

    @Test
    void toUser_shouldMatchAllFields() {
        User actual = personMapper.toUser(person);
        assertEquals(user, actual);
    }

    @Test
    void getUsername_shouldConcatenateFirstNameAndLastName() {
        String actual = personMapper.getUsername("firstName", "lastName");
        String expected = "firstName_lastName";

        assertEquals(expected, actual);
    }

    @Test
    void mapCountry_shouldReturnCountryEntityWithSameTitle() {
        Country actual = personMapper.mapCountry("country");
        Country expected = Country.builder().title("country").build();

        assertEquals(expected, actual);
    }

    @Test
    void getAboutMe_shouldProperlyConcatenateFieldsToAboutMe() {
        String actual = personMapper.getAboutMe(person);
        String expected = "about me: state - state, faculty - faculty, year of study - 2020, major - major, employer - employer";

        assertEquals(expected, actual);
    }
}