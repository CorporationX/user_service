package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
public class EventController {

    private final EventService eventService;

    @PostMapping()
    public EventDto create(@RequestBody @Valid EventDto eventDto) {
        return eventService.create(eventDto);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable("id") Long eventId) {
        return eventService.getEvent(eventId);
    }


    @GetMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody @Valid EventFilterDto eventFilterDto) {
        return eventService.getEventsByFilter(eventFilterDto);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable("id") Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping()
    public EventDto updateEvent(@RequestBody @Valid EventDto eventDto) {
        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/owned/{userId}")
    public List<Event> getOwnedEvents(@PathVariable Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participated/{userId}")
    public List<Event> getParticipatedEvents(@PathVariable Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}