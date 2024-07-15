package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;
    private long userId = 1L;
    private User user = User.builder()
            .id(userId)
            .build();

    @Test
    public void testFindNotExistingUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> userService.findUserById(userId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testFindExistingUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        User expected = userService.findUserById(userId);
        assertEquals(user, expected);
    }
}
