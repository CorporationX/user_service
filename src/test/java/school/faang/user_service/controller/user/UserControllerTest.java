package school.faang.user_service.controller.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    @Test
    void getUser() {
        assertDoesNotThrow(() -> userController.getUser(anyLong()));

        verify(userService).getUser(anyLong());
    }

    @Test
    void existsById() {
        assertDoesNotThrow(() -> userController.existsById(anyLong()));

        verify(userService).existsById(anyLong());
    }
}