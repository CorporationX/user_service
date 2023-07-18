package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@Controller("restController")
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

    private boolean checkValidation(EventDto event) {
        return event.getTitle() != null && !event.getTitle().isEmpty()
                && event.getStartDate() != null && event.getOwnerId() != null;
    }
}
