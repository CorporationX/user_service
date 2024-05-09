package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.filter.UserFilter;

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
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setCity("Rostov");
        userFilterDto.setExperience(500);

        userService.getPremiumUsers(userFilterDto);

        verify(userRepository, times(1)).findPremiumUsers();
    }
}
