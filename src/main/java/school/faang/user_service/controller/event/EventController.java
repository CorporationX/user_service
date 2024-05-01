package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exceptions.event.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto deleteEvent(long eventId) {
        return eventService.deleteEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public EventDto create(EventDto event) {
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
