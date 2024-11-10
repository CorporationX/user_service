package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidatorTest {
    private UserValidator userValidator;

    @BeforeEach
    public void setUp() {
        userValidator = new UserValidator();
    }

    @Test
    public void testValidId() {
        // arrange
        long userId = 5L;

        // act and assert
        assertDoesNotThrow(() -> userValidator.validateUserId(userId));
    }

    @Test
    public void testInvalidId() {
        // arrange
        long userId = -5L;

        // act and assert
        assertThrows(IllegalArgumentException.class,
                () -> userValidator.validateUserId(userId));
    }
}
