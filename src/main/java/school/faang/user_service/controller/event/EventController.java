package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.Validator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final Validator validator;

    public EventDto create(EventDto event) {
        validator.checkEventDto(event);
        return eventService.create(event);
    }

    public void getEvent(Long id) {
        eventService.getEvent(id);
    }

    public void getEventsByFilter(EventFilterDto filter) {
        eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(Long id) {
        eventService.deleteEvent(id);
    }

    public EventDto updateEvent(EventDto event) {
        validator.checkEventDto(event);
        return eventService.updateEvent(event);
    }

    public List<Event> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
