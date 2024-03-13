package school.faang.user_service.service.userMapperTest;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.entity.User;
import school.faang.user_service.userDto.UserDto;
import school.faang.user_service.userMapper.UserMapperImpl;

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
        UserDto userDto = userMapper.toUserDto(user);
        assertThat(userDto.getId()).isEqualTo(1L);
    }
}
