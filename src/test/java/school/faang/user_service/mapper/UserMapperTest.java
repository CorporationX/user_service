package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mydto.CountryDto;
import school.faang.user_service.dto.mydto.GoalDto;
import school.faang.user_service.dto.mydto.SkillDto;
import school.faang.user_service.dto.mydto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.mymappers.Country1MapperImpl;
import school.faang.user_service.mapper.mymappers.Goal1MapperImpl;
import school.faang.user_service.mapper.mymappers.Skill1MapperImpl;
import school.faang.user_service.mapper.mymappers.User1MapperImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class UserMapperTest {

    private Goal1MapperImpl goalMapper;
    private Skill1MapperImpl skillMapper;
    private Country1MapperImpl countryMapper;
    private User1MapperImpl userMapper;

    @BeforeEach
    void setUp() {
        goalMapper = new Goal1MapperImpl();
        skillMapper = new Skill1MapperImpl();
        countryMapper = new Country1MapperImpl();
        userMapper = new User1MapperImpl(goalMapper, skillMapper, countryMapper);
    }

    @Test
    void toDto_ShouldReturnDto() {
        UserDto actual = userMapper.toDto(buildUser(1L));

        assertEquals(buildUserDto(1L), actual);
    }

    @Test
    void toDtos_ShouldReturnDtos() {
        List<UserDto> actual = userMapper.toDtos(buildListOfUsers());

        assertIterableEquals(buildListOfDtos(), actual);
    }

    private User buildUser(long id) {
        return User.builder()
                .id(id)
                .username("name")
                .email("email")
                .phone("phone")
                .aboutMe("aboutMe")
                .active(true)
                .city("city")
                .experience(1)
                .followers(List.of(User.builder().id(1L).build(), User.builder().id(2L).build()))
                .followees(List.of(User.builder().id(3L).build(), User.builder().id(4L).build()))
                .mentors(List.of(User.builder().id(5L).build(), User.builder().id(6L).build()))
                .mentees(List.of(User.builder().id(7L).build(), User.builder().id(8L).build()))
                .country(Country.builder().id(1L).title("title").build())
                .goals(List.of(Goal.builder().id(1L).build(), Goal.builder().id(2L).build()))
                .skills(List.of(Skill.builder().id(1L).build(), Skill.builder().id(2L).build()))
                .build();
    }

    private UserDto buildUserDto(long id) {
        return UserDto.builder()
                .id(id)
                .username("name")
                .email("email")
                .phone("phone")
                .aboutMe("aboutMe")
                .active(true)
                .city("city")
                .experience(1)
                .followers(List.of(1L, 2L))
                .followees(List.of(3L, 4L))
                .mentors(List.of(5L, 6L))
                .mentees(List.of(7L, 8L))
                .country(CountryDto.builder().id(1L).title("title").build())
                .goals(List.of(
                        GoalDto.builder().id(1L).skillIds(Collections.emptyList()).build(),
                        GoalDto.builder().id(2L).skillIds(Collections.emptyList()).build()
                ))
                .skills(List.of(SkillDto.builder().id(1L).build(), SkillDto.builder().id(2L).build()))
                .build();
    }

    private List<User> buildListOfUsers() {
        return List.of(
                buildUser(1L),
                buildUser(2L),
                buildUser(3L)
        );
    }

    private List<UserDto> buildListOfDtos() {
        return List.of(
                buildUserDto(1L),
                buildUserDto(2L),
                buildUserDto(3L)
        );
    }
}
