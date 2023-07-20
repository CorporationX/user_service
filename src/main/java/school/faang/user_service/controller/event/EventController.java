package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        validate(event);
        return eventService.create(event);
    }

    private void validate(EventDto event) {
        if (event == null || event.getTitle() == null || event.getTitle().isBlank() || event.getStartDate() == null || event.getOwnerId() == null) {
            throw new DataValidationException("Event is invalid");
        }
    }

    public EventDto getEvent(long id) {
        return eventService.getEvent(id);
    }

    public void deleteEvent(long id) {
        eventService.deleteEvent(id);
    }

    public int updateEvent(EventDto event) {
        validate(event);
        return eventService.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter){
        return eventService.getEventsByFilter(filter);
    }
}
