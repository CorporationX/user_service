package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<UserFilter> userFilters;

    @Mock
    private UserMapper userMapper;

    @Test
    public void testGetPremiumUsers_IsRunFindPremiumUsers() {
        UserFilterDto userFilterDto = UserFilterDto.builder()
                .city("Rostov")
                .experience(500)
                .build();

        userService.getPremiumUsers(userFilterDto);

        verify(userRepository, times(1)).findPremiumUsers();
    }
}
