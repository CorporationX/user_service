package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    public EventDto create(EventDto event) {
        validate(event);
        eventService.create(event);
        return event;
    }

    public EventDto getEvent(long id) {
        return eventService.getEvent(id);
    }

    public List<EventDto> getEventsByFilter(@Valid EventFilterDto filter) {
      return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public void updateEvent(EventDto event) {
        validate(event);
        eventService.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
       return eventService.getParticipatedEvents(userId);
    }

    private void validate(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank() || event.getOwnerId() == null || event.getStartDate() == null) {
            throw new DataValidationException("Обязательный поля пустые");
        }
    }
 }
