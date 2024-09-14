package school.faang.user_service.validator.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventValidatorTest {
    private EventValidator validator = new EventValidator();

    @Test
    void testValidateEventsThrowsWithNull(){
        assertThrows(DataValidationException.class, () -> validator.validateEvents(null));
    }
}
