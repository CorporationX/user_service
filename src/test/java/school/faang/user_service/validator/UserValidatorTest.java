package school.faang.user_service.validator;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserValidatorTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserValidator userValidator;

    private static final long CORRECT_ID = 1L;
    private static final long INCORRECT_ID = 0L;

    @BeforeEach
    void setUp() {
        Mockito.when(userRepository.findById(CORRECT_ID))
                .thenReturn(Optional.of(new User()));
        Mockito.when(userRepository.findById(INCORRECT_ID))
                .thenReturn(Optional.empty());
    }

    @Test
    void validateUser_shouldThrowException() {
        assertThrows(RuntimeException.class,
                () -> userValidator.validateUser(INCORRECT_ID),
                "User with id: " + INCORRECT_ID + " does not exist");
    }

    @Test
    void validateUser_shouldNotThrowException() {
        assertNotNull(userValidator.validateUser(CORRECT_ID));
    }
}