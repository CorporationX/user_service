package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.EventValidator;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventValidator eventValidator;

    public EventDto create(EventDto event) {
        eventValidator.validateEvent(event);

    }
}
