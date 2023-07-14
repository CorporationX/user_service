package school.faang.user_service.controller.event;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@Controller
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/events")
    public ResponseEntity<EventDto> create(@RequestBody EventDto event) {
        validateEvent(event);
        EventDto createdEvent = eventService.create(event);
        return ResponseEntity.ok(createdEvent);
    }

    private void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isEmpty()) {
            throw new DataValidationException("Event title must not be empty");
        }
        if (event.getStartDate() == null || event.getOwnerId() == null) {
            throw new DataValidationException("Event must have a start date and an owner");
        }
    }
}
