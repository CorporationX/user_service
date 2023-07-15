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
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        var res = eventService.create(eventDto);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/get/{eventId}")
    public ResponseEntity<?> getEvent(long eventId) {
        checkId(eventId);
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<?>> getEventsByFilter(@RequestBody EventFilterDto filter) {
        List<EventDto> filteredEvents = eventService.getEventsByFilter(filter);
        return ResponseEntity.ok(filteredEvents);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable long id) {
        checkId(id);
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateEvent(@RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        eventService.updateEvent(eventDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-owned-events/{userId}")
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable long userId) {
        checkId(userId);
        List<EventDto> ownedEvents = eventService.getOwnedEvents(userId);
        return ResponseEntity.ok(ownedEvents);
    }

    private void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("Event start date cannot be null");
        }
        if (event.getOwnerId() == null || event.getOwnerId() < 0) {
            throw new DataValidationException("Event owner ID cannot be null or negative");
        }
    }

    private void checkId(long id) {
        if (id < 1) {
            throw new DataValidationException("id have to be > 0");
        }
    }
}
