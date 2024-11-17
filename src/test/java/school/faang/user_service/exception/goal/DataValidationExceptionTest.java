package school.faang.user_service.exception.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataValidationExceptionTest {

    @Test
    @DisplayName("Test Exception Message")
    void testExceptionMessage() {
        String message = "exception message";
        DataValidationException dataValidationException = new DataValidationException(message);

        assertEquals(message, dataValidationException.getMessage());
    }
}
