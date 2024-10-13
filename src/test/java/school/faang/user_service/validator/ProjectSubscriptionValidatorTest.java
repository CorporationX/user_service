package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class ProjectSubscriptionValidatorTest {

    @InjectMocks
    private ProjectSubscriptionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProjectSubscriptionValidator();
    }

    @Test
    void validateUser_WhenIdsMatch_ShouldNotThrowException() {
        long contextUserId = 1L;
        long userId = 1L;

        assertDoesNotThrow(() -> validator.validateUser(contextUserId, userId),
                "The validator should not throw an exception when the context user ID matches the user ID.");
    }

    @Test
    void validateUser_WhenIdsDoNotMatch_ShouldThrowDataValidationException() {
        long contextUserId = 1L;
        long userId = 2L;

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> validator.validateUser(contextUserId, userId),
                "The validator should throw a DataValidationException when the context user ID does not match the user ID.");

        String expectedMessage = String.format("A user can only subscribe themselves to a project. Context user id = %d, userId = %d", contextUserId, userId);
        String actualMessage = exception.getMessage();

        assert expectedMessage.equals(actualMessage) : "Error message should match the expected output.";
    }
}
