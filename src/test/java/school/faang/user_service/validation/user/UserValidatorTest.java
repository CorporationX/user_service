package school.faang.user_service.validation.user;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateUserExistsById_UserExists_ShouldNotThrow() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() ->
                userValidator.validateUserExistsById(5L));
    }

    @Test
    void validateUserExistsById_UserDoesntExist_ShouldThrowNoSuchElementException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () ->
                userValidator.validateUserExistsById(666L));
    }
}
