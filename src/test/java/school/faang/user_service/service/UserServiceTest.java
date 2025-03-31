package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final long USER_ID = 1L;
    private static final String USER_NAME = "Tony";
    private static final String USER_EMAIL = "TonyStark@Email.com";
    @Mock
    private UserRepository userRepository;

    @Mock
    private List<UserFilter> users;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetPremiumUsers() {
        User user = new User();
        UserDto userDto = UserDto.builder().id(USER_ID).username(USER_NAME).email(USER_EMAIL).build();
        UserFilterDto userFilterDto = new UserFilterDto();
        when(userRepository.findPremiumUsers()).thenReturn((Stream.of(user)));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getPremiumUsers(userFilterDto);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(USER_ID, result.get(0).getId());
        Assertions.assertEquals(USER_NAME, result.get(0).getUsername());
        verify(userRepository, times(1)).findPremiumUsers();
    }
}
