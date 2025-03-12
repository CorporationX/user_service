package school.faang.user_service.validator.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserValidator userValidator;

    @Test
    @DisplayName("Exists user by id - User not found")
    public void testCheckExistsByIdUserNotFound() {
        String errorMessage = "User not found";

        when(userRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userValidator.checkUserExistsById(anyLong()));
        verify(userRepository, times(1)).existsById(anyLong());
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Exists user by id - Successful")
    public void testCheckUserExistsById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> userValidator.checkUserExistsById(anyLong()));
        verify(userRepository, times(1)).existsById(anyLong());
    }
}
