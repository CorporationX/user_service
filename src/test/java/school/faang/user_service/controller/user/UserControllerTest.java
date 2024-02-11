package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.UserController;
import school.faang.user_service.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    @Test
    public void successDeactivationUserById() {
        long userId = 1L;
        userController.deactivationUserById(userId);
        Mockito.verify(userService).deactivationUserById(userId);
    }

}
