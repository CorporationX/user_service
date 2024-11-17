package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    long userId;

    @Test
    void validateUserByIdWrongId() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userValidator.validateUserById(1L));
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void validateUserByIdValidId() {
        when(userRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> userValidator.validateUserById(1L));
        verify(userRepository, times(1)).existsById(1L);
    }


}
