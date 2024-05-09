package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.user.UserController;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private UserFilterDto userFilterDto;

    @BeforeEach
    public void setUp() {
        userFilterDto = new UserFilterDto();
    }

    @Test
    public void getPremiumUsers_NullDtoTest() {
        userFilterDto = null;
        assertThrows(IllegalArgumentException.class, () -> userController.getPremiumUsers(userFilterDto));
    }

    @Test
    public void getPremiumUsers_IsRunGetPremiumUsers() {
        userController.getPremiumUsers(userFilterDto);
        verify(userService, times(1)).getPremiumUsers(userFilterDto);
    }
}