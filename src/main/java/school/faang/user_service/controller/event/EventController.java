package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        var res = eventService.create(eventDto);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable long eventId) {
        validateId(eventId);
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<?>> getEventsByFilter(@RequestBody EventFilterDto filter) {
        List<EventDto> filteredEvents = eventService.getEventsByFilter(filter);
        return ResponseEntity.ok(filteredEvents);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable long eventId) {
        validateId(eventId);
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    public ResponseEntity<?> updateEvent(@RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        eventService.updateEvent(eventDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/owned-events/{userId}")
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable long userId) {
        validateId(userId);
        List<EventDto> ownedEvents = eventService.getOwnedEvents(userId);
        return ResponseEntity.ok(ownedEvents);
    }

    @GetMapping("/participated-events/{userId}")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(@PathVariable long userId) {
        validateId(userId);
        List<EventDto> participatedEvents = eventService.getParticipatedEvents(userId);
        return ResponseEntity.ok(participatedEvents);
    }

    private void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("Event start date cannot be null");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("Event owner ID cannot be null");
        }
    }

    private void validateId(long id) {
        if (id < 1) {
            throw new DataValidationException("id have to be > 0");
        }
    }
}
