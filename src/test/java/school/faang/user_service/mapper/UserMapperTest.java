package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.entity.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void init() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void shouldReturnUserDtoWhenGetValidUserWithNonEmptyFields() {
        User user = getTestUser();

        UserDto userDto = userMapper.toUserDto(user);

        assertEquals(getTestUserDto(), userDto);
    }

    @Test
    void shouldReturnUserWhenGetValidUserDtoWithNonEmptyFields() {
        UserDto userDto = getTestUserDto();

        User user = userMapper.toUser(userDto);

        assertEquals(getTestUser(), user);
    }

    @Test
    void shouldReturnUserDtoWithNullFieldsWhenGetValidUserWithNullFields() {
        User user = getTestNullUser();

        UserDto userDto = userMapper.toUserDto(user);

        assertEquals(getTestNullUserDto(), userDto);
    }

    @Test
    void shouldReturnUserWithNullFieldsWhenGetValidUserDtoWithNullFields() {
        UserDto userDto = getTestNullUserDto();

        User user = userMapper.toUser(userDto);

        assertEquals(getTestNullUser(), user);
    }

    private User getTestUser() {
        return User.builder()
                .id(1L)
                .username("user1")
                .email("email1")
                .phone("phone1")
                .password("password1")
                .active(true)
                .aboutMe("aboutMe1")
                .country(Country.builder().id(1L).build())
                .followers(getTestUserList())
                .followees(getTestUserList())
                .ownedEvents(getTestEventList())
                .mentees(getTestUserList())
                .mentors(getTestUserList())
                .skills(getTestSkillsList())
                .build();
    }

    private UserDto getTestUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("user1")
                .email("email1")
                .phone("phone1")
                .password("password1")
                .active(true)
                .aboutMe("aboutMe1")
                .countryId(1L)
                .followersIds(getTestIdsList())
                .followeesIds(getTestIdsList())
                .ownedEventsIds(getTestIdsList())
                .menteesIds(getTestIdsList())
                .mentorsIds(getTestIdsList())
                .skillsIds(getTestIdsList())
                .build();
    }

    private User getTestNullUser() {
        return User.builder()
                .id(null)
                .username(null)
                .email(null)
                .phone(null)
                .password(null)
                .active(null)
                .aboutMe(null)
                .country(null)
                .followers(null)
                .followees(null)
                .ownedEvents(null)
                .mentees(null)
                .mentors(null)
                .skills(null)
                .build();
    }

    private UserDto getTestNullUserDto() {
        return UserDto.builder()
                .id(null)
                .username(null)
                .email(null)
                .phone(null)
                .password(null)
                .active(null)
                .aboutMe(null)
                .countryId(null)
                .followersIds(null)
                .followeesIds(null)
                .ownedEventsIds(null)
                .menteesIds(null)
                .mentorsIds(null)
                .skillsIds(null)
                .build();
    }

    private List<Long> getTestIdsList() {
        return new ArrayList<>(List.of(2L, 3L, 4L, 5L, 6L));
    }

    private List<User> getTestUserList() {
        return new ArrayList<>(List.of(
                User.builder().id(2L).build(),
                User.builder().id(3L).build(),
                User.builder().id(4L).build(),
                User.builder().id(5L).build(),
                User.builder().id(6L).build()
        ));
    }

    private List<Event> getTestEventList() {
        return new ArrayList<>(List.of(
                Event.builder().id(2L).build(),
                Event.builder().id(3L).build(),
                Event.builder().id(4L).build(),
                Event.builder().id(5L).build(),
                Event.builder().id(6L).build()
        ));
    }

    private List<Skill> getTestSkillsList() {
        return new ArrayList<>(List.of(
                Skill.builder().id(2L).build(),
                Skill.builder().id(3L).build(),
                Skill.builder().id(4L).build(),
                Skill.builder().id(5L).build(),
                Skill.builder().id(6L).build()
        ));
    }

}