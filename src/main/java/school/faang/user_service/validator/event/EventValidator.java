package school.faang.user_service.validator.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exceptions.event.DataValidationException;

@Component
public class EventValidator {
    public void validate(EventDto event) {
        if (event.getTitle() == null) {
            throw new DataValidationException("title can't be null - " + event);
        }
        if (event.getTitle().isBlank()) {
            throw new DataValidationException("title can't be blank - " + event);
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("start date can't be null - " + event);
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("event owner can't be null - " + event);
        }
    }
}
