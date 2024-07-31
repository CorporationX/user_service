package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EventController {

    private final EventService eventService;

    public EventDto create(@Valid EventDto event) {
        return eventService.create(event);
    }

    public EventDto getEventById(@Valid Long eventId) {
        return eventService.getEventById(eventId);
    }

    private List<EventDto> getEventsByFilter(EventFilterDto filters) {
        return eventService.getEventsByFilter(filters);
    }

    public void deleteEvent(@Valid Long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(@Valid Long eventId, @Valid EventDto event) {
        return eventService.updateEvent(eventId, event);
    }

    public List<EventDto> getOwnedEvents(@Valid Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(@Valid Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
