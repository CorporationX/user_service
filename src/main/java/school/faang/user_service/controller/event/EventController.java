package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventServiceImpl;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventServiceImpl eventServiceImpl;

    public EventDto create(EventDto event) {
        validate(event);
        eventServiceImpl.create(event);
        return event;
    }

    public EventDto getEvent(long id) {
        return eventServiceImpl.getEvent(id);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
      return eventServiceImpl.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        eventServiceImpl.deleteEvent(eventId);
    }

    public void updateEvent(EventDto event) {
        validate(event);
        eventServiceImpl.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventServiceImpl.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
       return eventServiceImpl.getParticipatedEvents(userId);
    }

    private void validate(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank() || event.getOwnerId() == null || event.getStartDate() == null) {
            throw new DataValidationException("Обязательный поля пустые");
        }
    }
 }
