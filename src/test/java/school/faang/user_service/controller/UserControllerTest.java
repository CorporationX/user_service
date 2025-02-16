package school.faang.user_service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.users.UsersService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UsersService usersService;
    @InjectMocks
    private UserController userController;

    @Test
    @DisplayName("Test get user")
    void getUser() {
        Long userId = 1L;
        userController.getUser(userId);
        Mockito.verify(usersService, Mockito.times(1)).getUser(userId);
    }
}