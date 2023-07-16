package school.faang.user_service.user_filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserFilter2Test {
    UserFilter2 userFilter2 = new UserFilter2();

    @Test
    void testFilterUsersWrongName() {
        User user = User.builder().username("John").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().namePattern("Name").build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongAbout() {
        User user = User.builder().aboutMe("").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().aboutPattern("I am a student").build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongEmail() {
        User user = User.builder().email(" ").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().emailPattern("email@example").build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongContact() {
        User user = User.builder().contacts(Collections.emptyList()).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().contactPattern("123456789").build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongCountry() {
        User user = User.builder().country(new Country(1L, " ", Collections.emptyList())).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().countryPattern("Peace").build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongCity() {
        User user = User.builder().city("City").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().cityPattern("Town").build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongPhone() {
        User user = User.builder().phone("1234567").build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().phonePattern("000").build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongSkill() {
        User user = User.builder().skills(Collections.emptyList()).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().skillPattern("Faster").build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongExperienceMin() {
        User user = User.builder().experience(10).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().experienceMin(11).build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersWrongExperienceMax() {
        User user = User.builder().experience(10).build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder().experienceMax(5).build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(0, userList.size());
    }

    @Test
    void testFilterUsersRightAllParameters() {
        User user = User.builder()
                .username("John Black")
                .aboutMe("I am a student in the university Harvard")
                .email("email@example")
                .contacts(Collections.emptyList())
                .country(new Country(1L, "All world", Collections.emptyList()))
                .city("New York city")
                .phone("123456789")
                .skills(Collections.emptyList())
                .experience(10)
                .build();
        Stream<User> userStream = Stream.of(user);
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .namePattern("John")
                .aboutPattern("I am a student")
                .emailPattern("@example")
                .contactPattern(" ")
                .countryPattern("world")
                .cityPattern("New")
                .phonePattern("12345")
                .skillPattern(null)
                .experienceMin(5)
                .experienceMax(15)
                .build();
        List<User> userList = userFilter2.filterUsers(userStream, userFilterDto);
        assertEquals(1, userList.size());
    }
}