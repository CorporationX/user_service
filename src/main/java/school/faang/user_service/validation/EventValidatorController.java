package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class EventValidatorController {

    public void validByTitleStartDateOwner(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Событие должно иметь название, и не должно быть пустым.");
        }
        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Событие должно иметь время начала.");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("Событие должно иметь id владельца.");
        }
    }
}