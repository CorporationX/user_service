package school.faang.user_service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserIdValidatorTest {

    @InjectMocks
    private UserIdValidator userIdValidator;

    private UserIdValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserIdValidator();
    }

    @Test
    void validateId_ValidId_DoesNotThrowException() {
        Assertions.assertDoesNotThrow(() -> validator.validateId(1L),
                "Should not throw exception for a positive ID");
        Assertions.assertDoesNotThrow(() -> validator.validateId(100L),
                "Should not throw exception for a large positive ID");
    }

    @Test
    void validateId_NullId_ThrowsIllegalArgumentException() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> validator.validateId(null),
                "Should throw IllegalArgumentException for null ID");
        Assertions.assertEquals("Id must not be blank", thrown.getMessage(),
                "Exception message should match");
    }

    @Test
    void validateId_ZeroId_ThrowsIllegalArgumentException() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> validator.validateId(0L),
                "Should throw IllegalArgumentException for zero ID");
        Assertions.assertEquals("Id must be positive", thrown.getMessage(),
                "Exception message should match");
    }

    @Test
    void validateId_NegativeId_ThrowsIllegalArgumentException() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> validator.validateId(-1L),
                "Should throw IllegalArgumentException for negative ID");
        Assertions.assertEquals("Id must be positive", thrown.getMessage(),
                "Exception message should match");
    }
}
