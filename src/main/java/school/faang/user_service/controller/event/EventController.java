package school.faang.user_service.controller.event;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.service.event.EventService;

@RestController
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService service;

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable("id") @Positive long id) {
        return service.getEvent(id);
    }
}
