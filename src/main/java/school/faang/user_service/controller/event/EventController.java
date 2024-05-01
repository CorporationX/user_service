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

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public EventDto updateEvent(EventDto event) {
        validate(event);
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
        validate(event);
        return eventService.create(event);
    }

    private void validate(EventDto event) {
        if (event.getTitle() == null) {
            throw new DataValidationException(String.format("title can't be null - %s", event));
        }
        if (event.getTitle().isBlank()) {
            throw new DataValidationException(String.format("title can't be blank - %s", event));
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException(String.format("start date can't be null - %s", event));
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException(String.format("event owner can't be null - %s", event));
        }
    }
}
