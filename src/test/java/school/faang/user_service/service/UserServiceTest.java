package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.validator.UserValidator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("testing getUser method")
    public void testGetUser() {
        long userId = 1L;
        User user = User.builder()
                .id(userId).build();
        when(userValidator.validateUserExistence(userId)).thenReturn(user);
        userService.getUser(userId);
        verify(userValidator, times(1)).validateUserExistence(userId);
    }
}