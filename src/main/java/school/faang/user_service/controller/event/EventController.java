package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

@Component
public class EventController {
    private final EventService eventService;


    public EventDto create(@Valid @RequestBody EventDto event) {

    }
}
