package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@Controller("eventController")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    public EventDto create(EventDto event){
        if (eventService.validation(event)) {
            return eventService.create(event);
        }else {
            throw new DataValidationException("Object is not valid");
        }
    }
}
