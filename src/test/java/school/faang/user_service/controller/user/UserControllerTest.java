package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.UserFilterDtoValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Spy
    private UserFilterDtoValidator userFilterDtoValidator;

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