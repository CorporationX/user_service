package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

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

        UserDto userDto = userMapper.toDto(user);

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

        UserDto userDto = userMapper.toDto(user);

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
                .aboutMe("aboutMe1")
                .country(Country.builder().id(1L).build())
                .build();
    }

    private UserDto getTestUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("user1")
                .email("email1")
                .phone("phone1")
                .aboutMe("aboutMe1")
                .countryId(1L)
                .build();
    }

    private User getTestNullUser() {
        return User.builder()
                .id(null)
                .username(null)
                .email(null)
                .phone(null)
                .aboutMe(null)
                .country(null)
                .build();
    }

    private UserDto getTestNullUserDto() {
        return UserDto.builder()
                .id(null)
                .username(null)
                .email(null)
                .phone(null)
                .aboutMe(null)
                .countryId(null)
                .build();
    }

}