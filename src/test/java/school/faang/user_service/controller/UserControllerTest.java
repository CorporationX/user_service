package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.UserService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    UserService userService;
    @InjectMocks
    UserController userController;

    @Test
    void getUser() {
        long userId = 1L;
        UserDto userDto = new UserDto();
        when(userService.getUser(userId)).thenReturn(userDto);
        userController.getUser(userId);
        verify(userService, times(1)).getUser(userId);
    }
}