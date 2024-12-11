package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.entity.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    public void testToDto() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("John Doe");
        user.setCity("New York");

        // Act
        UserSubResponseDto userDto = userMapper.toUserSubResponseDto(user);

        // Assert
        assertEquals(user.getId(), userDto.id());
        assertEquals(user.getUsername(), userDto.username());
    }
}
