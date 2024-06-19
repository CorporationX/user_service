package school.faang.user_service.controller.event;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import static school.faang.user_service.exception.message.ExceptionMessage.INVALID_EVENT_DATES_EXCEPTION;
import static school.faang.user_service.exception.message.ExceptionMessage.NULL_EVENT_ID_EXCEPTION;

@Component
class EventControllerValidation {
    public void validateEventDates(EventDto event) {
        if (event.getStartDate().isAfter(event.getEndDate())) {
            throw new DataValidationException(INVALID_EVENT_DATES_EXCEPTION.getMessage());
        }
    }

    public void validateEventId(@NotNull EventDto event) {
        if (event.getId() == null) {
            throw new DataValidationException(NULL_EVENT_ID_EXCEPTION.getMessage());
        }
    }
}
