package school.faang.user_service.exception.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @InjectMocks
    private EventValidator eventValidator;

    @Test
    void shouldValidateTitleEvent() {
        EventDto eventDto = new EventDto();

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventDto);
        });
        assertEquals("Не заполнено название", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateStartDateEvent() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Test");
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventDto);
        });
        assertEquals("Не заполнена дата", dataValidationException.getMessage());
    }

    @Test
    void shouldValidateOwnerIdEvent() {
        LocalDateTime now = LocalDateTime.now();
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Test");
        eventDto.setStartDate(now);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
        {
            eventValidator.validate(eventDto);
        });
        assertEquals("Не заполнен пользователь", dataValidationException.getMessage());
    }
}