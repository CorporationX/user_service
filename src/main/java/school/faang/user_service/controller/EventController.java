package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.EventService;
import school.faang.user_service.validator.EventValidator;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @GetMapping("/byFilter/")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filterDto) {
        eventValidator.checkFilterNotNull(filterDto);
        return eventService.getEventsByFilter(filterDto);
    }

    @PostMapping("/create")
    public EventDto create(@RequestBody EventDto eventDto) {
        eventValidator.validateEventInController(eventDto);
        return eventService.create(eventDto);
    }

    @GetMapping("/getAll/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @PutMapping
    public EventDto updateEvent(@RequestBody EventDto eventDto) {
        eventValidator.validateEventInController(eventDto);
        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/participated/{userId}")
    public List<EventDto> getParticipatedEventsByUserId(@PathVariable long userId) {
        return eventService.getParticipatedEventsByUserId(userId);
    }

    @GetMapping("/{userId}")
    public EventDto getEvent(Long eventId) {
        return eventService.getEventDto(eventId);
    }

    @DeleteMapping("/delete/{eventId}")
    public void deleteEvent(@PathVariable long eventId) {
        eventService.deleteEventById(eventId);
    }

}