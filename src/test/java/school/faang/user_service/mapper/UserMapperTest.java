package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private static final long USER_ID_1 = 1L;
    private static final long USER_ID_2 = 2L;
    private static final String USERNAME_1 = "user1";
    private static final String USERNAME_2 = "user2";
    private static final String EMAIL_1 = "user1@example.com";
    private static final String EMAIL_2 = "user2@example.com";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "testuser@example.com";

    private User createUser(long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    @Test
    void testUserToUserDto() {
        User user = createUser(USER_ID_1, TEST_USERNAME, TEST_EMAIL);

        UserDto userDto = userMapper.toDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void testUsersToUserDtos() {
        User user1 = createUser(USER_ID_1, USERNAME_1, EMAIL_1);
        User user2 = createUser(USER_ID_2, USERNAME_2, EMAIL_2);

        List<User> users = List.of(user1, user2);
        List<UserDto> userDtos = userMapper.toListUserDto(users);

        assertEquals(2, userDtos.size());

        assertEquals(user1.getId(), userDtos.get(0).getId());
        assertEquals(user1.getUsername(), userDtos.get(0).getUsername());
        assertEquals(user1.getEmail(), userDtos.get(0).getEmail());

        assertEquals(user2.getId(), userDtos.get(1).getId());
        assertEquals(user2.getUsername(), userDtos.get(1).getUsername());
        assertEquals(user2.getEmail(), userDtos.get(1).getEmail());
    }

    @Test
    void testUsersToUserDtos_NullInput() {
        List<UserDto> userDtos = userMapper.toListUserDto(null);
        assertNull(userDtos);
    }

    @Test
    void testUserToUserDto_NullInput() {
        UserDto userDto = userMapper.toDto(null);
        assertNull(userDto);
    }
}
