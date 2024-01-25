package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.event.EventValidator;
import school.faang.user_service.service.event.EventService;

import java.util.List;


@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @PostMapping("/create")
    public EventDto create(@RequestBody EventDto event) {
        eventValidator.validate(event);
        return eventService.create(event);
    }

    @GetMapping("/{id}/get")
    public EventDto getEvent(@PathVariable("id") long eventId) {
        return eventService.getEvent(eventId);
    }

    @GetMapping("/get/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filterDto) {
        return eventService.getEventsByFilter(filterDto);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteEvent(@PathVariable("id") long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping("/update")
    public EventDto updateEvent(@RequestBody EventDto eventDto) {
        eventValidator.validate(eventDto);
        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/{id}/get/filter")
    public List<EventDto> getOwnedEvents(@PathVariable("id") long userId, @RequestBody EventFilterDto filterDto) {
        return eventService.getOwnedEvents(userId, filterDto);
    }

    @GetMapping("/{id}/get/filter/participated")
    public List<EventDto> getParticipatedEvents(@PathVariable("id") long userId, @RequestBody EventFilterDto filterDto) {
        return eventService.getParticipatedEvents(userId, filterDto);
    }

}
