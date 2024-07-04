package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.service.event.EventService;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    public EventReadDto create(EventCreateEditDto eventCreateEditDto) {
        return eventService.create(eventCreateEditDto);
    }
}
