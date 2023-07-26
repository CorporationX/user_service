package school.faang.user_service.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    @DisplayName("Convert User entity to UserDto")
    void toDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");

        UserDto userDto = userMapper.toDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getUsername(), userDto.getUsername());
    }

    @Test
    @DisplayName("Convert List<User> entities to ListDto")
    void toListDtoTest() {
        List<User> users = List.of(
            User.builder().id(1L).username("username").build()
        );

        List<UserDto> userDtoList = userMapper.toDtoList(users);

        assertEquals(1, userDtoList.size());
        assertEquals(users.get(0).getId(), userDtoList.get(0).getId());
        assertEquals(users.get(0).getUsername(), userDtoList.get(0).getUsername());
    }
}