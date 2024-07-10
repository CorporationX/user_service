package school.faang.user_service.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

public class DataValidationExceptionTest {
    @Test
    public void testMessage() {
        String message = "Invalid data";
        DataValidationException exception = new DataValidationException(message);
        assertThat(exception.getMessage()).isEqualTo(message);
    }
}
