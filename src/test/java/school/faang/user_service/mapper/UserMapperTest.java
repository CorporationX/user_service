package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapperImpl;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserMapperTest {

    @Mock
    private User user;

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    public void testtoDto() {
        when(user.getId()).thenReturn(1L);
        UserDto userDto = userMapper.toDto(user);
        assertThat(userDto.getId()).isEqualTo(1L);
    }

    @Test
    void toDto() {
        List<User> users = List.of(
                User.builder().id(1L).build()
        );

        List<UserDto> userDtoList = userMapper.toDto(users);

        assertEquals(1, userDtoList.size());
        assertEquals(users.get(0).getId(), userDtoList.get(0).getId());
    }
}
