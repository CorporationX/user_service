package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.service.user.UserService;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void testDeactivateUser() {
        userService.deactivateUser(1L);

        verify(userService, times(1)).deactivateUser(1L);
    }
}