package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    private UserMapper userMapper;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();

        user = User.builder()
                .id(1L)
                .username("username")
                .email("user@email")
                .phone("12345678")
                .aboutMe("aboutUser")
                .country(Country.builder().title("country").build())
                .city("city")
                .experience(10)
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("user@email")
                .phone("12345678")
                .aboutMe("aboutUser")
                .city("city")
                .experience(10)
                .build();
    }

    @Test
    void userToUserDto_shouldMatchAllFields() {
        UserDto result = userMapper.toDto(user);
        assertAll(() -> {
            assertEquals(1L, result.getId());
            assertEquals("username", result.getUsername());
            assertEquals("user@email", result.getEmail());
            assertEquals("12345678", result.getPhone());
            assertEquals("aboutUser", result.getAboutMe());
            assertEquals("country", result.getCountry());
            assertEquals("city", result.getCity());
            assertEquals(10, result.getExperience());
        });
    }

    @Test
    void userDtoToUser_shouldMatchAllFields() {
        User result = userMapper.toUser(userDto);
        assertAll(() -> {
            assertEquals(1L, result.getId());
            assertEquals("username", result.getUsername());
            assertEquals("user@email", result.getEmail());
            assertEquals("12345678", result.getPhone());
            assertEquals("aboutUser", result.getAboutMe());
            assertEquals("city", result.getCity());
            assertEquals(10, result.getExperience());
        });
    }
}