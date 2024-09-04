package school.faang.user_service.validator;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;

@Component
@NoArgsConstructor
public class EventValidator {

    public void validateEvent(EventDto eventDto) {
        checkTitle(eventDto);
        checkStartDate(eventDto);
    }

    public void checkTitle(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Event title can not be null or empty");
        }
    }

    public void checkStartDate(EventDto eventDto) {
        LocalDateTime eventStartDate = eventDto.getStartDate();
        if (eventStartDate == null) {
            throw new DataValidationException("Event start date can not be null");
        }
    }

}
