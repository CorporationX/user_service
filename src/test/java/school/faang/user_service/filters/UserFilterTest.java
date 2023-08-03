package school.faang.user_service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFilterTest {
    private final List<UserFilter> userFilters = List.of(
            new UserNameFilter(),
            new UserAboutFilter(),
            new UserEmailFilter(),
            new UserContactFilter(),
            new UserCountryFilter(),
            new UserCityFilter(),
            new UserPhoneFilter(),
            new UserSkillFilter(),
            new UserExperienceMinFilter(),
            new UserExperienceMaxFilter()
    );

    @Test
    void testFilterUsersWrongName() {
        User user = User.builder()
                .username("John")
                .build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().namePattern("Name").build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongAbout() {
        User user = User.builder().aboutMe("").build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().aboutPattern("I am a student").build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongEmail() {
        User user = User.builder().email("@gmail.com").build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().emailPattern("email@example.com").build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongContact() {
        User user = User.builder().contacts(Collections.emptyList()).build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().contactPattern("123456789").build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongCountry() {
        User user = User.builder().country(new Country(1L, "World", Collections.emptyList())).build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().countryPattern("Ru").build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongCity() {
        User user = User.builder().city("City").build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().cityPattern("Town").build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongPhone() {
        User user = User.builder().phone("1234567").build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().phonePattern("000").build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongSkill() {
        User user = User.builder().skills(Collections.emptyList()).build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().skillPattern("Faster").build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongExperienceMin() {
        User user = User.builder().experience(10).build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().experienceMin(11).build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongExperienceMax() {
        User user = User.builder().experience(10).build();
        List<User> userList = new ArrayList<>(List.of(user));
        UserFilterDto userFilterDto = UserFilterDto.builder().experienceMax(5).build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersRightAllParameters() {
        User user1 = User.builder()
                .username("John Black")
                .aboutMe("I am a student in the Harvard university")
                .email("black@example.com")
                .contacts(Collections.emptyList())
                .country(new Country(1L, "America", Collections.emptyList()))
                .city("New York city")
                .phone("88001234567")
                .skills(Collections.emptyList())
                .experience(10)
                .build();
        User user2 = User.builder()
                .username("Jonathan White")
                .aboutMe("I am a student in the Oxford university")
                .email("white@example.com")
                .contacts(Collections.emptyList())
                .country(new Country(1L, "England", Collections.emptyList()))
                .city("London city")
                .phone("88007894561")
                .skills(Collections.emptyList())
                .experience(15)
                .build();
        User user3 = User.builder()
                .username("Dan Green")
                .aboutMe("I am a professor in the Harvard university")
                .email("green@example.com")
                .contacts(Collections.emptyList())
                .country(new Country(1L, "Mexico", Collections.emptyList()))
                .city("Los Angeles city")
                .phone("88004562378")
                .skills(Collections.emptyList())
                .experience(20)
                .build();
        List<User> userList = new ArrayList<>(List.of(user1, user2, user3));
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .namePattern("Jo")
                .aboutPattern("I am a student")
                .emailPattern("@example.com")
                .contactPattern(" ")
                .countryPattern("")
                .cityPattern("city")
                .phonePattern("8800")
                .skillPattern(null)
                .experienceMin(5)
                .experienceMax(15)
                .build();
        userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(userFilterDto))
                .forEach(userFilter -> userFilter.apply(userList, userFilterDto));
        assertEquals(2, userList.size());
    }
}
