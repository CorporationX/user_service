package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscription.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

public class UserMapperTest {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    public void testToDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");

        UserDto userDto = userMapper.toDto(user);

        assertEquals(Optional.of(user.getId()), Optional.of(userDto.getId()));
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    public void testToEntity() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("jane_doe");
        userDto.setEmail("jane@example.com");

        User user = userMapper.toEntity(userDto);

        assertEquals(Optional.ofNullable(userDto.getId()), Optional.ofNullable(user.getId()));
        assertEquals(userDto.getUsername(), user.getUsername());
        assertEquals(userDto.getEmail(), user.getEmail());
    }

    @Test
    public void testToDtoList() {
        User user1 = User.builder().id(1L).username("adil").email("adil@mail.ru").build();
        User user2 = User.builder().id(2L).username("pasha").email("pasha@mail.ru").build();

        List<User> userList = List.of(user1, user2);

        List<UserDto> userDtoList = userMapper.toDtoList(userList);

        assertEquals(userList.size(), userDtoList.size());
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            UserDto userDto = userDtoList.get(i);

            assertEquals(Optional.of(user.getId()), Optional.of(userDto.getId()));
            assertEquals(user.getUsername(), userDto.getUsername());
            assertEquals(user.getEmail(), userDto.getEmail());
        }
    }

    @Test
    public void testToEntityList() {
        List<UserDto> userDtoList = List.of(
                new UserDto(1L, "john_doe", "john@example.com"),
                new UserDto(2L, "jane_doe", "jane@example.com")
        );

        List<User> userList = userMapper.toEntityList(userDtoList);

        assertEquals(userDtoList.size(), userList.size());
        for (int i = 0; i < userDtoList.size(); i++) {
            UserDto userDto = userDtoList.get(i);
            User user = userList.get(i);

            assertEquals(Optional.ofNullable(userDto.getId()), Optional.ofNullable(user.getId()));
            assertEquals(userDto.getUsername(), user.getUsername());
            assertEquals(userDto.getEmail(), user.getEmail());
        }
    }
}
