package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        if (!(checkValidation(event))) {
            throw new DataValidationException("Object is not valid");
        }
        return eventService.create(event);
    }

    public EventDto getEvent(long id) {
        return eventService.getEvent(id);
    }

    public void getEventsByFilter(EventFilterDto filter) {
        eventService.getEventsByFilter(filter);
    }

    public void updateEvent(EventDto event) {
        if (!(checkValidation(event))) {
            throw new DataValidationException("The event did not pass validation when updating the event");
        }
        eventService.updateEvent(event);
    }

    public void getOwnedEvents(long userId) {
        eventService.getOwnedEvents(userId);
    }

    public void deleteEvent(long id) {
        eventService.deleteEvent(id);
    }

    private boolean checkValidation(EventDto event) {
        return event.getTitle() != null && !event.getTitle().isEmpty()
                && event.getStartDate() != null && event.getOwnerId() != null;
    }
}