package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {
    private static final long USER_ID = 1L;
    private static final long INCORRECT_USER_ID = 3L;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(userRepository.existsById(INCORRECT_USER_ID)).thenReturn(false);
    }

    @Test
    void validateUsers_shouldNotThrowException() {
        assertDoesNotThrow(() -> userService.validateUsers(USER_ID));
    }

    @Test
    void validateUsers_shouldThrowException() {
        assertThrows(EntityNotFoundException.class,
                () -> userService.validateUsers(INCORRECT_USER_ID),
                "User with id " + INCORRECT_USER_ID + " not found.");
    }
}