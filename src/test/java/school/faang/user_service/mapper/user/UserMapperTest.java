package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private UserMapper userMapper;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("John");
        user1.setEmail("john@test.com");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("Dan");
        user2.setEmail("dan@test.com");
    }

    @Test
    void testToUserDto() {
        UserDto userDto = userMapper.toUserDto(user1);

        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getUsername(), userDto.getUsername());
        assertEquals(user1.getEmail(), userDto.getEmail());
    }

    @Test
    void testToListUserDtos() {
        List<User> users = List.of(user1, user2);

        List<UserDto> userDtos = userMapper.toListUserDtos(users);

        assertEquals(users.size(), userDtos.size());
        assertEquals(user1.getUsername(), userDtos.get(0).getUsername());
        assertEquals(user2.getUsername(), userDtos.get(1).getUsername());

    }
}
