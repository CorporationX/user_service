package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class UserFilterTest {

    private UserFilterDto filters;
    private UserFilterDto nullFilters;
    private List<User> users;
    private User userAnna;
    private User userBeast;
    private User userAnne;
    private User userConor;
    private User userVasya;
    private boolean actualResultBoolean;
    private Stream<User> expectedResultUsers;
    private Stream<User> actualResultUsers;

    @BeforeEach
    public void setUp() {
        filters = UserFilterDto.builder()
                .namePattern("An")
                .aboutPattern("actor")
                .emailPattern("@gmail.com")
                .contactPattern("e")
                .countryPattern("Russia")
                .cityPattern("Orel")
                .phonePattern("67")
                .skillPattern("skill2")
                .experienceMin(1)
                .experienceMax(5)
                .build();

        nullFilters = UserFilterDto.builder().build();

        userAnna = User.builder()
                .username("Anna Kern")
                .aboutMe("Pushkin's muse")
                .email("annak@examle.com")
                .contacts(Collections.emptyList())
                .country(new Country(1L, "Russia", Collections.emptyList()))
                .city("Orel")
                .phone("")
                .skills(List.of(Skill.builder().id(1L).title("skill1Anna").build(), Skill.builder().id(2L).title("skill2Anna").build()))
                .experience(4)
                .build();
        userBeast = User.builder()
                .username("Mr Beast")
                .aboutMe("Youtuber, voice actor, entrepreneur and philanthropist")
                .email("mrbeast@gmail.com")
                .contacts(Collections.emptyList())
                .country(new Country(2L, "USA", Collections.emptyList()))
                .city("Wichita")
                .phone("(123) 123-4567")
                .skills(List.of(Skill.builder().id(3L).title("skill1Beast").build(), Skill.builder().id(4L).title("skill2Beast").build(), Skill.builder().id(5L).title("skill3Beast").build()))
                .experience(7)
                .build();
        userAnne = User.builder()
                .username("Anne Hathaway")
                .aboutMe("Actress and singer")
                .email("annehathaway@gmail.com")
                .contacts(Collections.emptyList())
                .country(new Country(2L, "USA", Collections.emptyList()))
                .city("New York")
                .phone("(123) 765-4321")
                .skills(List.of(Skill.builder().id(6L).title("skill1Anne").build()))
                .experience(5)
                .build();
        userConor = User.builder()
                .username("Conor McGregor")
                .aboutMe("Professional mixed martial artist, professional boxer and actor")
                .email("conor@gmail.com")
                .contacts(Collections.emptyList())
                .country(new Country(3L, "Irland", Collections.emptyList()))
                .city("Dublin")
                .phone("01 123 4567")
                .skills(List.of(Skill.builder().id(7L).title("skill1Conor").build(), Skill.builder().id(8L).title("skill2Conor").build()))
                .experience(6)
                .build();
        userVasya = User.builder()
                .username("Vasya Vasya")
                .aboutMe("Leading engineer of the production and technical department")
                .email("vasya38reg@mail.ru")
                .contacts(Collections.emptyList())
                .country(new Country(1L, "Russia", Collections.emptyList()))
                .city("Irkutsk")
                .phone("8-999-1234567")
                .skills(Collections.emptyList())
                .experience(5)
                .build();

        users = List.of(userAnna, userBeast, userAnne, userConor, userVasya);
    }

    @Test
    public void testNameIsApplicable() {
        UserNameFilter userNameFilter = new UserNameFilter();

        actualResultBoolean = userNameFilter.isApplicable(nullFilters);
        assertFalse(actualResultBoolean);

        actualResultBoolean = userNameFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);
    }

    @Test
    public void testAboutIsApplicable() {
        UserAboutFilter userAboutFilter = new UserAboutFilter();

        actualResultBoolean = userAboutFilter.isApplicable(nullFilters);
        assertFalse(actualResultBoolean);

        actualResultBoolean = userAboutFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);
    }

    @Test
    public void testEmailIsApplicable() {
        UserEmailFilter userEmailFilter = new UserEmailFilter();

        actualResultBoolean = userEmailFilter.isApplicable(nullFilters);
        assertFalse(actualResultBoolean);

        actualResultBoolean = userEmailFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);
    }

    @Test
    public void testContactIsApplicable() {
        UserContactFilter userContactFilter = new UserContactFilter();

        actualResultBoolean = userContactFilter.isApplicable(nullFilters);
        assertFalse(actualResultBoolean);

        actualResultBoolean = userContactFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);
    }

    @Test
    public void testCountryIsApplicable() {
        UserCountryFilter userCountryFilter = new UserCountryFilter();

        actualResultBoolean = userCountryFilter.isApplicable(nullFilters);
        assertFalse(actualResultBoolean);

        actualResultBoolean = userCountryFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);
    }

    @Test
    public void testCityIsApplicable() {
        UserCityPatternFilter userCityPatternFilter = new UserCityPatternFilter();

        actualResultBoolean = userCityPatternFilter.isApplicable(nullFilters);
        assertFalse(actualResultBoolean);

        actualResultBoolean = userCityPatternFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);
    }

    @Test
    public void testPhoneIsApplicable() {
        UserPhoneFilter userPhoneFilter = new UserPhoneFilter();

        actualResultBoolean = userPhoneFilter.isApplicable(nullFilters);
        assertFalse(actualResultBoolean);

        actualResultBoolean = userPhoneFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);
    }

    @Test
    public void testSkillIsApplicable() {
        UserSkillFilter userSkillFilter = new UserSkillFilter();

        actualResultBoolean = userSkillFilter.isApplicable(nullFilters);
        assertFalse(actualResultBoolean);

        actualResultBoolean = userSkillFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);
    }

    @Test
    public void testExperienceIsApplicable() {
        UserExperienceSpanFilter userExperienceFilter = new UserExperienceSpanFilter();

        actualResultBoolean = userExperienceFilter.isApplicable(nullFilters);
        assertFalse(actualResultBoolean);

        actualResultBoolean = userExperienceFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);

        filters.setExperienceMin(5);
        filters.setExperienceMax(2);
        actualResultBoolean = userExperienceFilter.isApplicable(filters);
        assertFalse(actualResultBoolean);

        filters.setExperienceMin(5);
        filters.setExperienceMax(0);
        actualResultBoolean = userExperienceFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);

        filters.setExperienceMin(2);
        filters.setExperienceMax(2);
        actualResultBoolean = userExperienceFilter.isApplicable(filters);
        assertTrue(actualResultBoolean);
    }

    @Test
    public void testNameApply() {
        UserNameFilter userNameFilter = new UserNameFilter();

        expectedResultUsers = Stream.of(userAnna, userAnne);
        actualResultUsers = userNameFilter.apply(users.stream(), filters);

        assertArrayEquals(expectedResultUsers.toArray(), actualResultUsers.toArray());
    }

    @Test
    public void testAboutApply() {
        UserAboutFilter userAboutFilter = new UserAboutFilter();

        expectedResultUsers = Stream.of(userBeast, userConor);
        actualResultUsers = userAboutFilter.apply(users.stream(), filters);

        assertArrayEquals(expectedResultUsers.toArray(), actualResultUsers.toArray());
    }

    @Test
    public void testEmailApply() {
        UserEmailFilter userEmailFilter = new UserEmailFilter();

        expectedResultUsers = Stream.of(userBeast, userAnne, userConor);
        actualResultUsers = userEmailFilter.apply(users.stream(), filters);

        assertArrayEquals(expectedResultUsers.toArray(), actualResultUsers.toArray());
    }

    @Test
    public void testContactApply() {
        List<Contact> contactsBeast = List.of(Contact.builder().id(1L).user(userBeast).contact("@mrbeast").type(ContactType.INSTAGRAM).build());
        userBeast.setContacts(contactsBeast);

        List<Contact> contactsAnne = List.of(Contact.builder().id(2L).user(userAnne).contact("@annehathaway").type(ContactType.INSTAGRAM).build(),
                Contact.builder().id(3L).user(userAnne).contact("@annehathaway21").type(ContactType.TELEGRAM).build());
        userAnne.setContacts(contactsAnne);

        List<Contact> contactsConor = List.of(Contact.builder().id(4L).user(userConor).contact("@thenotoriousmma").type(ContactType.INSTAGRAM).build(),
                Contact.builder().id(5L).user(userConor).contact("@Conor_McGregora").type(ContactType.TELEGRAM).build(),
                Contact.builder().id(6L).user(userConor).contact("conor_mcgregor").type(ContactType.VK).build());
        userConor.setContacts(contactsConor);

        List<Contact> contactsVasya = List.of(Contact.builder().id(7L).user(userVasya).contact("@vasyavasya").type(ContactType.INSTAGRAM).build(),
                Contact.builder().id(8L).user(userConor).contact("@vasya2x").type(ContactType.TELEGRAM).build(),
                Contact.builder().id(9L).user(userConor).contact("vasya_vasya").type(ContactType.VK).build());
        userVasya.setContacts(contactsVasya);

        UserContactFilter userContactFilter = new UserContactFilter();

        expectedResultUsers = Stream.of(userBeast, userAnne, userConor);
        actualResultUsers = userContactFilter.apply(users.stream(), filters);

        assertArrayEquals(expectedResultUsers.toArray(), actualResultUsers.toArray());
    }

    @Test
    public void testCountryApply() {
        UserCountryFilter userCountryFilter = new UserCountryFilter();

        expectedResultUsers = Stream.of(userAnna, userVasya);
        actualResultUsers = userCountryFilter.apply(users.stream(), filters);

        assertArrayEquals(expectedResultUsers.toArray(), actualResultUsers.toArray());
    }

    @Test
    public void testCityApply() {
        UserCityPatternFilter userCityPatternFilter = new UserCityPatternFilter();

        expectedResultUsers = Stream.of(userAnna);
        actualResultUsers = userCityPatternFilter.apply(users.stream(), filters);

        assertArrayEquals(expectedResultUsers.toArray(), actualResultUsers.toArray());
    }

    @Test
    public void testPhoneApply() {
        UserPhoneFilter userPhoneFilter = new UserPhoneFilter();

        expectedResultUsers = Stream.of(userBeast, userConor, userVasya);
        actualResultUsers = userPhoneFilter.apply(users.stream(), filters);

        assertArrayEquals(expectedResultUsers.toArray(), actualResultUsers.toArray());
    }

    @Test
    public void testSkillApply() {
        UserSkillFilter userSkillFilter = new UserSkillFilter();

        expectedResultUsers = Stream.of(userAnna, userBeast, userConor);
        actualResultUsers = userSkillFilter.apply(users.stream(), filters);

        assertArrayEquals(expectedResultUsers.toArray(), actualResultUsers.toArray());
    }

    @Test
    public void testExperienceApply() {
        UserExperienceSpanFilter userExperienceFilter = new UserExperienceSpanFilter();

        expectedResultUsers = Stream.of(userAnna, userAnne, userVasya);
        actualResultUsers = userExperienceFilter.apply(users.stream(), filters);

        assertArrayEquals(expectedResultUsers.toArray(), actualResultUsers.toArray());
    }
}

