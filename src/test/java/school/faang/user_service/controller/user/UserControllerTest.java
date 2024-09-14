package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.user.UserService;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testGetPremiumUsers() {
        userController.getPremiumUsers(any());
        verify(userService, times(1)).getPremiumUsers(any());
    }

    @Test
    void testDeactivateUserProfile() {
        userController.deactivateUserProfile(1L);

        verify(userService).deactivateUserProfile(1L);
    }
}
