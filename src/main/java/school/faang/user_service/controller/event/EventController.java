package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.create(eventDto);
    }

    private void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }
        if (event.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }

        if (event.getStartDate() == null) {
            throw new DataValidationException("Event start date cannot be null");
        }

        if (event.getOwnerId() == null || event.getOwnerId() < 0) {
            throw new DataValidationException("Event owner ID cannot be null");
        }
    }
}
