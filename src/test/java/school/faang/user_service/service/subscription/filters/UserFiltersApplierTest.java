package school.faang.user_service.service.subscription.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserFiltersApplierTest {

    private List<UserFilter> userFilters;

    private UserFiltersApplier userFiltersApplier;

    private List<User> users;
    private final UserFilterDto userFilterDto = UserFilterDto.builder()
            .namePattern("nameTest")
            .aboutPattern("aboutMeTest")
            .emailPattern("emailTest")
            .contactPattern("contactTest")
            .countryPattern("countryTest")
            .cityPattern("cityTest")
            .phonePattern("phoneTest")
            .skillPattern("skillTest")
            .experienceMin(3)
            .experienceMax(10)
            .build();

    @BeforeEach
    void setUp() {
        userFilters = new ArrayList<>();
        userFilters.add(new NameFilter());
        userFilters.add(new AboutMeFilter());
        userFilters.add(new EmailFilter());
        userFilters.add(new ContactFilter());
        userFilters.add(new CountryFilter());
        userFilters.add(new CityFilter());
        userFilters.add(new PhoneFilter());
        userFilters.add(new SkillFilter());
        userFilters.add(new ExperienceMinFilter());
        userFilters.add(new ExperienceMaxFilter());
        userFiltersApplier = new UserFiltersApplier(userFilters);
        users = initUsers();
    }

    @Test
    @DisplayName("Successfully filtered users")
    void testFilterUsers_SuccessfullyFilteredUsers() {
        var expected = List.of(users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(), userFilterDto);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering empty stream of user")
    void testFilterUsers_FilteringEmptyStreamOfUser() {
        var expected = new ArrayList<>();

        var result = userFiltersApplier.applyFilters(Stream.empty(), userFilterDto);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with empty userFilterDto")
    void testFilterUsers_FilteringUsersWithEmptyUserFilterDto() {
        var expected = users;

        var result = userFiltersApplier.applyFilters(users.stream(), UserFilterDto.builder().build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with name filter")
    void testFilterUsers_FilteringUsersWithOneFilter() {
        var expected = List.of(users.get(0), users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().namePattern("nameTest").build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with aboutMe filter")
    void testFilterUsers_FilteringUsersWithAboutMeFilter() {
        var expected = List.of(users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().aboutPattern("aboutMeTest").build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with email filter")
    void testFilterUsers_FilteringUsersWithEmailFilter() {
        var expected = List.of(users.get(1), users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().emailPattern("emailTest").build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with contact filter")
    void testFilterUsers_FilteringUsersWithContactFilter() {
        var expected = List.of(users.get(2), users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().contactPattern("contactTest").build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with country filter")
    void testFilterUsers_FilteringUsersWithCountryFilter() {
        var expected = List.of(users.get(3), users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().countryPattern("countryTest").build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with city filter")
    void testFilterUsers_FilteringUsersWithCityFilter() {
        var expected = List.of(users.get(4), users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().cityPattern("cityTest").build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with phone filter")
    void testFilterUsers_FilteringUsersWithPhoneFilter() {
        var expected = List.of(users.get(5), users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().phonePattern("phoneTest").build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with skill filter")
    void testFilterUsers_FilteringUsersWithSkillFilter() {
        var expected = List.of(users.get(6), users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().skillPattern("skillTest").build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with experienceMin filter")
    void testFilterUsers_FilteringUsersWithExperienceMinFilter() {
        var expected = List.of(users.get(7), users.get(8), users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().experienceMin(3).build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with experienceMax filter")
    void testFilterUsers_FilteringUsersWithExperienceMaxFilter() {
        var expected = List.of(users.get(8), users.get(9), users.get(10), users.get(users.size() - 1));

        var result = userFiltersApplier.applyFilters(users.stream(),
                UserFilterDto.builder().experienceMax(10).build());

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Filtering users with null arguments")
    void testFilterUsers_FilteringUsersWithNullArguments() {
        assertThrows(NullPointerException.class, () -> userFiltersApplier.applyFilters(null, userFilterDto));
        assertThrows(NullPointerException.class, () -> userFiltersApplier.applyFilters(users.stream(), null));
        assertThrows(NullPointerException.class, () -> userFiltersApplier.applyFilters(null, null));
    }

    private List<User> initUsers() {
        return List.of(
                User.builder()
                        .id(1L)
                        .username("userToFilter_nameTest")
                        .email("test")
                        .build(),
                User.builder()
                        .id(2L)
                        .username("userToFilter")
                        .email("emailTest")
                        .contacts(List.of(Contact.builder().build()))
                        .build(),
                User.builder()
                        .id(3L)
                        .contacts(List.of(Contact.builder()
                                .contact("contactTest")
                                .build()))
                        .build(),
                User.builder()
                        .id(4L)
                        .country(Country.builder()
                                .id(1L)
                                .title("countryTest")
                                .build())
                        .build(),
                User.builder()
                        .id(5L)
                        .country(Country.builder().build())
                        .city("cityTest")
                        .build(),
                User.builder()
                        .id(6L)
                        .phone("phoneTest")
                        .build(),
                User.builder()
                        .id(7L)
                        .skills(List.of(Skill.builder()
                                .id(1L)
                                .title("skillTest")
                                .build()))
                        .build(),
                User.builder()
                        .id(8L)
                        .experience(15)
                        .build(),
                User.builder()
                        .id(9L)
                        .experience(5)
                        .build(),
                User.builder()
                        .id(10L)
                        .experience(2)
                        .build(),
                User.builder()
                        .id(11L)
                        .username("test")
                        .aboutMe("test")
                        .email("test")
                        .contacts(List.of(Contact.builder()
                                .id(1L)
                                .contact("test")
                                .build()))
                        .country(Country.builder()
                                .id(1L)
                                .title("test")
                                .build())
                        .city("test")
                        .phone("test")
                        .skills(List.of(Skill.builder()
                                .id(1L)
                                .title("test")
                                .build()))
                        .experience(0)
                        .build(),
                User.builder()
                        .id(12L)
                        .username("userNotToFilter_nameTest")
                        .aboutMe("aboutMeTest")
                        .email("emailTest")
                        .contacts(List.of(Contact.builder()
                                .id(1L)
                                .contact("contactTest")
                                .build()))
                        .country(Country.builder()
                                .id(1L)
                                .title("countryTest")
                                .build())
                        .city("cityTest")
                        .phone("phoneTest")
                        .skills(List.of(Skill.builder()
                                .id(1L)
                                .title("skillTest")
                                .build()))
                        .experience(5)
                        .build()
        );
    }
}
