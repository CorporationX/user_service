package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("events")
@Tag(name = "Events")
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Get participated events by userId")
    @GetMapping("users/participated/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @Operation(summary = "Get owned events by userId")
    @GetMapping("users/owned/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @Operation(summary = "Update event")
    @PutMapping
    public EventDto updateEvent(@RequestBody EventDto event) {
        return eventService.updateEvent(event);
    }

    @Operation(summary = "Delete event")
    @DeleteMapping("delete/{eventId}")
    public EventDto deleteEvent(@PathVariable long eventId) {
        return eventService.deleteEvent(eventId);
    }

    @Operation(summary = "Get events by filter")
    @GetMapping("filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @Operation(summary = "Get event by eventId")
    @GetMapping("{eventId}")
    public EventDto getEvent(@PathVariable long eventId) {
        return eventService.getEvent(eventId);
    }

    @Operation(summary = "Create event")
    @PostMapping
    public EventDto create(@RequestBody EventDto event) {
        return eventService.create(event);
    }
}
