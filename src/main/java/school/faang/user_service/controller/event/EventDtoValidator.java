package school.faang.user_service.controller.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class EventDtoValidator {
    public void validate(EventDto eventDto) {
        if (eventDto.getId() == null) {
            throw new DataValidationException("eventId can't be null");
        } else if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("title can't be null or empty");
        } else if (eventDto.getStartDate() == null) {
            throw new DataValidationException("getStartDate can't be null");
        } else if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("ownerId can't be null");
        }
    }
}
