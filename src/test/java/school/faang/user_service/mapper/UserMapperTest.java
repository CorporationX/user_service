package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    @DisplayName("Должен корректно маппить User в UserDto")
    void shouldMapUserToUserDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");

        UserDto userDto = userMapper.toDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    @DisplayName("Должен вернуть null, если входной объект User равен null")
    void shouldReturnNullIfUserIsNull() {
        UserDto userDto = userMapper.toDto(null);

        assertNull(userDto);
    }

    @Test
    @DisplayName("Должен вернуть UserDto с null полями, если поля User равны null")
    void shouldMapUserWithNullFieldsToUserDto() {
        User user = new User();
        user.setId(1L);

        UserDto userDto = userMapper.toDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertNull(userDto.getUsername());
        assertNull(userDto.getEmail());
    }
}
