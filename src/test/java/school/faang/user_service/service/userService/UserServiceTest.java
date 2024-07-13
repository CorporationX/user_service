package school.faang.user_service.service.userService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserFilter userFilter;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        List<UserFilter> userFilters = List.of(userFilter);
        userService = new UserService(userRepository, userMapper, userFilters);
    }

    @Test
    @DisplayName("Test get premium users")
    public void testGetPremiumUsers() {
        userService.getPremiumUsers(new UserFilterDto());
        Mockito.verify(userRepository, Mockito.times(1)).findPremiumUsers();
    }
}
