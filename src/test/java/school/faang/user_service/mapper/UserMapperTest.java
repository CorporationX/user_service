package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private UserMapper userMapper = new UserMapperImpl();

    @Test
    public void testToDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("John Doe");
        user.setCity("New York");

        // Act
        UserDto userDto = userMapper.toDto(user);

        // Assert
        assertEquals(user.getId(), userDto.id());
        assertEquals(user.getUsername(), userDto.username());
    }

    @Test
    public void testToUser() {
        // Arrange
        UserDto userDto = new UserDto(1L, "John Doe", "johndoe@gmail.com");

        // Act
        User user = userMapper.toEntity(userDto);

        // Assert
        assertEquals(userDto.id(), user.getId());
        assertEquals(userDto.username(), user.getUsername());
    }
}
