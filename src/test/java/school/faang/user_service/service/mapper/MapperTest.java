package school.faang.user_service.service.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.UserMapperImpl;

@ExtendWith(MockitoExtension.class)
public class MapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    private User user = User.builder().id(1L).active(true).build();
    private UserDto userDto = UserDto.builder().id(1L).active(true).build();

    @Test
    public void testMapperToDto() {
        User actualUser = userMapper.toEntity(userDto);
        UserDto actualUserDto = userMapper.toDto(actualUser);
        Assertions.assertEquals(user, actualUser);
        Assertions.assertEquals(userDto, actualUserDto);
    }
}
