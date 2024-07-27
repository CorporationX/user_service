package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

@Component
public class Validator {
    public boolean validateUpdatingEvent(EventDto eventDto) {

        return !eventDto.getTitle().isBlank()
               && !eventDto.getStartDate().toString().isBlank()
               && eventDto.getOwnerId() != null;
    }
}
