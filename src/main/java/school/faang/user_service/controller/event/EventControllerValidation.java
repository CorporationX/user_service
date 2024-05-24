package school.faang.user_service.controller.event;

import lombok.NonNull;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

import static school.faang.user_service.exception.ExceptionMessage.INVALID_EVENT_END_DATE_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.INVALID_EVENT_START_DATE_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_EVENT_OWNER_ID_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NULL_OR_BLANK_EVENT_TITLE_EXCEPTION;

@Component
class EventControllerValidation {
    public void validateEvent(@NonNull EventDto event) {

        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException(NULL_OR_BLANK_EVENT_TITLE_EXCEPTION.getMessage());
        }

        if (event.getStartDate() == null || event.getStartDate().isBefore(LocalDateTime.now())) {
            throw new DataValidationException(INVALID_EVENT_START_DATE_EXCEPTION.getMessage());
        }

        if (event.getOwnerId() == null) {
            throw new DataValidationException(NULL_EVENT_OWNER_ID_EXCEPTION.getMessage());
        }

        if (event.getEndDate() != null && event.getStartDate().isAfter(event.getEndDate())) {
            throw new DataValidationException(INVALID_EVENT_END_DATE_EXCEPTION.getMessage());
        }
    }
}
