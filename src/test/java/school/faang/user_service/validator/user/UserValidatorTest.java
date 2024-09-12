package school.faang.user_service.validator.user;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Test
    @DisplayName("Ошибка валидации если передели null")
    void testUserIdIsNullOrElseThrowValidationException() {
        assertThrows(ValidationException.class,
                () -> userValidator.checkUserIdIsNotNull(null),
                "User id can't be null");
    }
}