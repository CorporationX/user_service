package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validate.event.EventValidate;

@Component
@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventValidate eventValidate;

    @PostMapping("/event")
    public EventDto create(@RequestBody EventDto eventDto) {
        eventValidate.validateEvent(eventDto);
        return eventService.create(eventDto);
    }
}