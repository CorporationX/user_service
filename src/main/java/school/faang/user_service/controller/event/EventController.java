package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@Controller
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/events")
    @ResponseBody
    public EventDto create(@RequestBody EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    private void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isEmpty()) {
            throw new DataValidationException("Event title is required");
        }

        if (event.getStartDate() == null) {
            throw new DataValidationException("Event start date is required");
        }

        if (event.getOwnerId() == null) {
            throw new DataValidationException("Event owner is required");
        }
    }
}
