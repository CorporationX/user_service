package school.faang.user_service.Utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidatorUtilTest {

    @Test
    void validateOptional_NullValue_ThrowsEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, ()
                -> ValidatorUtil.validateOptional(Optional.ofNullable(null), "Entity not found"));
    }

    @Test
    void validateOptional_ValidValue_ThrowsEntityNotFoundException() {
        assertDoesNotThrow(() -> ValidatorUtil.validateOptional(Optional.ofNullable(new Object()), "Entity not found"));
    }

    @Test
    void validateNull_NullValue_ThrowsDataValidationException() {
        assertThrows(DataValidationException.class, () -> ValidatorUtil.validateNull(null));
    }

    @Test
    void validateNull_ValidValue_DoesNotThrowsException() {
        assertDoesNotThrow(() -> ValidatorUtil.validateNull(new Object()));
    }
}
