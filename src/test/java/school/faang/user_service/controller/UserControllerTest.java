package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.user.UserController;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @Mock
    private UserContext userContext;
    @Mock
    private SearchAppearanceEventPublisher eventPublisher;
    @InjectMocks
    private UserController userController;


    @Test
    public void successDeactivationUserById() {
        long userId = 1L;
        userController.deactivateUserById(userId);
        Mockito.verify(userService).deactivationUserById(userId);
    }
}
