package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
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

    public void getEvent(long id) {
        eventService.getEvent(id);
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

    public void getOwnedEvents(long userId) {
        eventService.getOwnedEvents(userId);
    }

    public void getParticipatedEvents(long userId) {
        eventService.getParticipatedEvents(userId);
    }



    private EventDto validateForUpdate(EventDto event) {
        try {
            if (event.getTitle() != null || event.getOwnerId() != null || event.getStartDate() != null) {
                return event;
            }
        } catch (Exception e) {
            throw new DataValidationException(e.getMessage());
        }
        return null;
    }

    private EventDto validateForCreate(EventDto event) {
       try {
           if (event.getTitle() != null || event.getOwnerId() != null || event.getStartDate() != null) {
               return event;
           }
       } catch (Exception e) {
           throw new NullPointerException("event null");
       }
       return null;
    }


}
