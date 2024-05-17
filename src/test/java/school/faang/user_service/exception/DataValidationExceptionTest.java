package school.faang.user_service.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
public class DataValidationExceptionTest {
    @Test
    public void testExceptionMessage() {
        String message = "exception";
        DataValidationException exception = new DataValidationException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testExceptionType() {
        String message = "exception";
        DataValidationException exception = new DataValidationException(message);

        assertInstanceOf(DataValidationException.class, exception);
    }
}
