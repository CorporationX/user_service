package school.faang.user_service.controller.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exceptions.event.DataValidationException;
import school.faang.user_service.service.event.EventService;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public EventDto create(@NonNull EventDto event) {
        if (isValid(event)) {
            return eventService.create(event);
        } else {
            throw new DataValidationException(String.format("not valid event - %s", event));
        }
    }

    private boolean isValid(EventDto event) {
        return event.getTitle() != null &&
                !event.getTitle().isBlank() &&
                event.getStartDate() != null &&
                event.getOwnerId() != null;
    }
}
