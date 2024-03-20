package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Events", description = "Endpoints for managing events")
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Create an event")
    @PostMapping
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        return eventService.create(eventDto);
    }

    @Operation(summary = "Get event by id")
    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable long eventId) {
        return eventService.getEvent(eventId);
    }

    @Operation(summary = "Get filtered list of all events")
    @PostMapping("/filtered")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filters) {
        return eventService.getEventsByFilter(filters);
    }

    @Operation(summary = "Delete an event")
    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable long eventId) {
        eventService.deleteEvent(eventId);
    }

    @Operation(summary = "Update an existing event")
    @PutMapping
    public EventDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        return eventService.updateEvent(eventDto);
    }

    @Operation(summary = "Get events, which user with given ID owns")
    @GetMapping("/user/{userId}/owned")
    public List<EventDto> getOwnedEvents(@PathVariable long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @Operation(summary = "Get events, which user with given ID participates in")
    @GetMapping("/user/{userId}/participated")
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
