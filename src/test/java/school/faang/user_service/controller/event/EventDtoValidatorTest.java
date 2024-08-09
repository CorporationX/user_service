package school.faang.user_service.controller.event;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class EventDtoValidatorTest {
    private EventDtoValidator validator = new EventDtoValidator();
    private EventDto eventDto = new EventDto();

    {
        eventDto.setId(1L);
        eventDto.setTitle("event");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setOwnerId(0L);
    }

    @Test
    public void testValidateWithNullId() {
        eventDto.setId(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("eventId can't be null", exception.getMessage());
    }

    @Test
    public void testValidateWithNullTitle() {
        eventDto.setTitle(null);

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
        eventDto.setStartDate(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("getStartDate can't be null", exception.getMessage());
    }

    @Test
    public void testValidateWithNullOwnerId() {
        eventDto.setOwnerId(null);

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> validator.validate(eventDto));
        assertEquals("ownerId can't be null", exception.getMessage());
    }

    @Test
    public void testValidate() {
        assertDoesNotThrow(() -> validator.validate(eventDto));
    }
}
