package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventValidator eventValidator;
    private final EventService eventService;

    @PostMapping
    public EventDto create(@RequestBody EventDto eventDto) {
        eventValidator.validateEvent(eventDto);
        return eventService.create(eventDto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventDto(@PathVariable long eventId) {
        return eventService.getEventDto(eventId);
    }

    @GetMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filterDto) {
        return eventService.getEventsByFilter(filterDto);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping
    public EventDto updateEvent(@RequestBody EventDto eventDto) {
        eventValidator.validateEvent(eventDto);
        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/owned/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participated/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
