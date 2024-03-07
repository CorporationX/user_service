package school.faang.user_service.service.UserMapperTest;

import org.junit.jupiter.api.Test;
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

@SpringBootTest
public class UserMapperTest {
    @Mock
    private User userMock;

    @Test
    public void testToUserDto() {
        UserMapper userMapper = new UserMapperImpl();
        long userId = 1L;
        String username = "snoop";
        String email = "junit@gmail.com";
        String phone = "+78005553535";
        Mockito.when(userMock.getId()).thenReturn(userId);
        Mockito.when(userMock.getUsername()).thenReturn(username);
        Mockito.when(userMock.getEmail()).thenReturn(email);
        Mockito.when(userMock.getPhone()).thenReturn(phone);
        UserDto userDto = userMapper.toUserDto(userMock);
        assertThat(userDto.getId()).isEqualTo(userId);
        assertThat(userDto.getUsername()).isEqualTo(username);
        assertThat(userDto.getEmail()).isEqualTo(email);
        assertThat(userDto.getPhone()).isEqualTo(phone);
    }

    @Test
    public void testToUserDto2() {
        UserMapper userMapper = new UserMapperImpl();
        List<User> mentors = new ArrayList<>();
        List<User> mentees = new ArrayList<>();
        Mockito.when(userMock.getMentees()).thenReturn(mentees);
        Mockito.when(userMock.getMentors()).thenReturn(mentors);
        UserDto userDto = userMapper.toUserDto(userMock);
        assertThat(userDto.getMentees()).isNull();
        assertThat(userDto.getMentors()).isNull();
    }
}
