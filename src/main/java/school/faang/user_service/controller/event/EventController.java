package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;


    public EventDto create(EventDto event) {
        validateForCreate(event);
        eventService.create(event);
        return event;
    }

    public EventDto getEvent(long id) {
        return eventService.getEvent(id);
    }

    public void getEventsByFilter(EventFilterDto filter) {
        eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public void updateEvent(EventDto event) {
        validateForUpdate(event);
        eventService.updateEvent(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
       return eventService.getParticipatedEvents(userId);
    }



    private EventDto validateForUpdate(EventDto event) {
        if (event.getTitle() != null || event.getOwnerId() != null || event.getStartDate() != null) {
            return event;
        } else {
            throw new DataValidationException("");
        }
    }

    private EventDto validateForCreate(EventDto event) {
           if (event.getTitle() != null && event.getOwnerId() != null && event.getStartDate() != null) {
               return event;
           } else {
               throw new IllegalArgumentException("Обязательные поля пустые");
           }
    }
}
