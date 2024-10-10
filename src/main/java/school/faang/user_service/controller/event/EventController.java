package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.controller.ApiPath;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.event.EventNotFoundException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.EVENT)
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto> create(@Valid @RequestBody EventDto eventDto) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.create(eventDto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable("eventId") long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEvent(eventId));
    }

    @GetMapping()
    public ResponseEntity<List<EventDto>> getEventsByFilter(@RequestBody EventFilterDto eventFilterDto) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventsByFilter(eventFilterDto));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("eventId") long eventId) {
        try {
            eventService.deleteEvent(eventId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (EventNotFoundException e) {
            log.error("ID: {} Event not in database", eventId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping
    public ResponseEntity<EventDto> updateEvent(EventDto event) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.updateEvent(event));
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable("userId") long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getOwnedEvents(userId));
    }

    @GetMapping("/all/participant/{userId}")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getParticipatedEvents(userId));
    }
}
