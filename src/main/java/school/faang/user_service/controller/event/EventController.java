package school.faang.user_service.controller.event;

import io.lettuce.core.dynamic.annotation.Param;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@RestController
@RequestMapping("api/events")
@Data
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("api/create")
    public EventDto create(@RequestBody EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    @GetMapping("/get/eventId")
    public EventDto getEvent(@PathVariable Long eventId){
            return eventService.getEvent(eventId);
    }
    @DeleteMapping("/delete/eventId}")
    public EventDto deleteEvent(@PathVariable Long eventId){
        return eventService.deleteEvent(eventId);
    }

    private void validateEvent(EventDto event) {
        if (StringUtils.isEmpty(event.getTitle())) {
            throw new DataValidationException("The title of the event can't be empty");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("The date of start can't be null");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("The ownerID can't be null ");
        }
    }
}
