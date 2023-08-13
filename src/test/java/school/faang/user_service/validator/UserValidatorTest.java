package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {
    @InjectMocks
    private UserValidator userValidator;

    @Test
    public void givenUserDoesNotExist_whenValidateUser_thenThrowException() {
        assertThrows(EntityNotFoundException.class, () -> {
            userValidator.validateUserId(1L);
        });
    }
}
