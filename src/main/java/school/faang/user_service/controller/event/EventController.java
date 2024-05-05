package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public EventDto updateEvent(EventDto event) {
        return eventService.updateEvent(event);
    }

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
        return eventService.create(event);
    }
}
