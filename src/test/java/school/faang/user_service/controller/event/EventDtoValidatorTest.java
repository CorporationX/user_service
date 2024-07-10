package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventDtoValidatorTest {
    private EventDtoValidator validator = new EventDtoValidator();
    private EventDto eventDto = new EventDto();

    @Test
    public void testValidateWithNullTitle() {
        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("title can't be null or empty", exception.getMessage());
    }

    @Test
    public void testValidateWithBlankTitle() {
        eventDto.setTitle("  ");

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("title can't be null or empty", exception.getMessage());
    }

    @Test
    public void testValidateWithNullStartDate() {
        eventDto.setTitle("event");
        eventDto.setStartDate(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("getStartDate can't be null", exception.getMessage());
    }

    @Test
    public void testValidateWithNullOwnerId() {
        eventDto.setTitle("event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(0L);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("ownerId can't be 0", exception.getMessage());
    }
}
