package school.faang.user_service.controller.event;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@AllArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventControllerValidation eventControllerValidation;

    public EventDto create(EventDto event) {
        eventControllerValidation.validateEvent(event);

        return eventService.create(event);
    }

    public EventDto getEvent(@NonNull Long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(@NonNull EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(@NonNull Long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto event) {
        eventControllerValidation.validateEvent(event);

        return eventService.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(@NonNull Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(@NonNull Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
