package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Event API", description = "API for managing events")
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @PostMapping
    @Operation(summary = "Create an event", description = "Creates a new event")
    public EventDto create(@RequestBody EventDto eventDto) {
        if (!eventValidator.validateEventDto(eventDto)) {
            throw new DataValidationException("Не валидные данные в EventDto");
        }
        return eventService.create(eventDto);
    }

    @GetMapping("/{eventId}")
    @Operation(summary = "Get an event", description = "Retrieves an event by its ID")
    public EventDto getEvent(@PathVariable @Parameter(description = "ID of the event to retrieve") Long eventId) {
        if (!eventValidator.validateId(eventId)) {
            throw new DataValidationException("Не валидный id");
        }
        return eventService.getEvent(eventId);
    }

    @DeleteMapping("/{eventId}")
    @Operation(summary = "Delete an event", description = "Deletes an event by its ID")
    public void deleteEvent(@PathVariable @Parameter(description = "ID of the event to delete") Long eventId) {
        if (!eventValidator.validateId(eventId)) {
            throw new DataValidationException("Не валидный id");
        }
        eventService.deleteEvent(eventId);
    }

    @PutMapping
    @Operation(summary = "Update an event", description = "Updates an existing event")
    public EventDto updateEvent(@RequestBody EventDto eventDto) {
        if (!eventValidator.validateEventDto(eventDto)) {
            throw new DataValidationException("Не валидные данные в EventDto");
        }
        return eventService.updateEvent(eventDto);
    }

    @PostMapping("/filter")
    @Operation(summary = "Get events by filter", description = "Retrieves events based on the specified filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @GetMapping("/owned/{userId}")
    @Operation(summary = "Get owned events", description = "Retrieves events owned by the specified user")
    public List<EventDto> getOwnedEvents(@PathVariable @Parameter(description = "ID of the user whose events are to be retrieved") long userId) {
        if (!eventValidator.validateId(userId)) {
            throw new DataValidationException("Не валидный id");
        }
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participated/{userId}")
    @Operation(summary = "Get participated events", description = "Retrieves events participated by the specified user")
    public List<EventDto> getParticipatedEvents(@PathVariable @Parameter(description = "ID of the user whose participated events are to be retrieved") long userId) {
        if (!eventValidator.validateId(userId)) {
            throw new DataValidationException("Не валидный id");
        }
        return eventService.getParticipatedEvents(userId);
    }

}
