package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    public EventDto create(EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    public EventDto getEvent(Long id) {
        return eventService.getEvent(id);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public long deleteEvent(long eventId) {
        return eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto event) {
        validateEvent(event);
        return eventService.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);

    }

    private void validateEvent(EventDto event) {

        if (event == null) {
            throw new DataValidationException("The event cannot be null.");
        }
        if (event.getTitle().isBlank() || event.getTitle() == null) {
            throw new DataValidationException("The title cannot be null or empty.");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("The start date is required.");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("The event must have an associated user (ownerId)");
        }

    }

}


