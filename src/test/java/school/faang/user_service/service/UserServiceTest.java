package school.faang.user_service.service;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Test: User exists")
    public void testFindUserByIdPositive() {
        Long userId = 1L;
        when(userService.isUserExist(userId)).thenReturn(true);
        assertTrue(userService.isUserExist(userId));
    }

    @Test
    @DisplayName("Test: User does not exists")
    public void testFindUserByIdNegative() {
        Long userId = 1L;
        when(userService.isUserExist(userId)).thenReturn(false);
        assertFalse(userService.isUserExist(userId));
    }
}