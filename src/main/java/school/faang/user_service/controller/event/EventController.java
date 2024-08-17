package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.Create;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.Update;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/events")
@Tag(name = "Event", description = "Event handler")
public class EventController {
    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Create new event")
    public EventDto create(@Validated(Create.class) @RequestBody EventDto eventDto) {
        return eventService.create(eventDto);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Gen event by ID")
    public EventDto getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/filter")
    @Operation(summary = "Gen events by filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Delete event by ID")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Operation(summary = "Update event")
    public EventDto updateEvent(@Validated(Update.class) @RequestBody EventDto eventDto) {
        return eventService.updateEvent(eventDto);
    }

    @GetMapping
    @Operation(summary = "Get all events owned by User")
    public List<EventDto> getOwnedEvents(
            @RequestParam("userId") @Min(value = 1L, message = "User id can't be less than 1") long userId) {
        return eventService.getOwnedEvents(userId);
    }
    @GetMapping("/participation")
    @Operation(summary = "Get all events participated by User")
    public List<EventDto> getParticipatedEvents(
            @RequestParam("userId") @Min(value = 1L, message = "User id can't be less than 1") long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
