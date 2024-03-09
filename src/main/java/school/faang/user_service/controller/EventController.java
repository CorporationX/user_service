package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.validator.EventValidator;
import school.faang.user_service.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventValidator eventValidator;
    private final EventService eventService;

    @PostMapping("/create")
    public EventDto create(@RequestBody EventDto event) {
        eventValidator.validate(event.getTitle(), event.getStartDate(), event.getOwnerId());
        return eventService.create(event);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable("eventId") long eventId) {
        return eventService.getEvent(eventId);
    }

    @GetMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filterDto) {
        return eventService.getEventsByFilter(filterDto);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable("eventId") long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable("eventId") long eventId, @RequestBody EventUpdateDto eventUpdateDto) {
        eventValidator.validate(eventUpdateDto.getTitle(), eventUpdateDto.getStartDate(), eventUpdateDto.getOwnerId());
        return eventService.updateEvent(eventId, eventUpdateDto);
    }

    @GetMapping("/{userId}/filter")
    public List<EventDto> getOwnedEvents(@PathVariable("userId") long userId, @RequestBody EventFilterDto filterDto) {
        return eventService.getOwnedEvents(userId, filterDto);
    }

    @GetMapping("/{userId}/filter/participated")
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") long userId, @RequestBody EventFilterDto filterDto) {
        return eventService.getParticipatedEvents(userId, filterDto);
    }

}
