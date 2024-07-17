package school.faang.user_service.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {
    @InjectMocks
    private UserMapperImpl userMapper;
    private final long id = 1L;
    private final String userName = "Username";
    private final String email = "email@gmail.com";
    private final User user = User.builder()
            .id(id)
            .username(userName)
            .email(email)
            .build();

    @Test
    public void toDto() {
        UserDto userDto = userMapper.toDto(user);
        Assertions.assertEquals(userDto.getId(), id);
        Assertions.assertEquals(userDto.getEmail(), email);
        Assertions.assertEquals(userDto.getUsername(), userName);
    }

    @Test
    public void toEntity() {
        UserDto userDto = new UserDto(id, userName, email);
        User resultUser = userMapper.toEntity(userDto);
        Assertions.assertEquals(resultUser.getId(), id);
        Assertions.assertEquals(resultUser.getEmail(), email);
        Assertions.assertEquals(resultUser.getUsername(), userName);
    }
}
