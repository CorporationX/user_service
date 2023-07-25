package school.faang.user_service.service.user;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

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
        when(userService.findUserById(userId)).thenReturn(true);
        assertTrue(userService.findUserById(userId));
    }

    @Test
    @DisplayName("Test: User does not exists")
    public void testFindUserByIdNegative() {
        Long userId = 1L;
        when(userService.findUserById(userId)).thenReturn(false);
        assertFalse(userService.findUserById(userId));
    }
}