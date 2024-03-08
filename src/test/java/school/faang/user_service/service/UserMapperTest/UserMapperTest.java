package school.faang.user_service.service.UserMapperTest;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.UserDto.UserDto;
import school.faang.user_service.UserMapper.UserMapper;
import school.faang.user_service.UserMapper.UserMapperImpl;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserMapperTest {

    @Mock
    private User user;

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    public void testToUserDto() {
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("username");
        UserDto userDto = userMapper.toUserDto(user);
        assertThat(userDto.getId()).isEqualTo(1L);
        assertThat(userDto.getUsername()).isEqualTo("username");
    }
}
