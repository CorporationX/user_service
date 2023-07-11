package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

@Component("eventController")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    public EventDto create(EventDto event){
        if (eventService.validation(event)) {
            return eventService.create(event);
        }else {
            throw new IllegalArgumentException();
        }
    }
}
