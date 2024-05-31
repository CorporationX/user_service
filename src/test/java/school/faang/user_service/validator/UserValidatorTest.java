package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {
    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    private Long userId;

    @BeforeEach
    public void setUd() {
        userId = 1L;
    }

    @Test
    public void testValidateUserInDBWitchIsExists() {
        when(userRepository.existsById(userId)).thenReturn(true);
        assertDoesNotThrow(() -> userValidator.validateUserExists(userId));
    }

    @Test
    public void testValidateUserInDBWitchIsNotExists() {
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> userValidator.validateUserExists(userId));
    }
}
