package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/events")
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDto> create(@RequestBody EventDto event) {
        EventDto createdEvent = eventService.create(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @GetMapping(value = "/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDto> getEvent(@PathVariable long eventId) {
        return ResponseEntity.ok(eventService.getEventDto(eventId));
    }

    @GetMapping(value = "/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventDto>> getEventsByFilter(EventFilterDto filter) {
        return ResponseEntity.ok(eventService.getEventsByFilter(filter));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventDto> updateEvent(@RequestBody EventDto eventDto) {
        return ResponseEntity.ok(eventService.updateEvent(eventDto));
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable long userId) {
       return ResponseEntity.ok(eventService.getOwnedEvents(userId));
    }

    @GetMapping(value = "/participated/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
       return ResponseEntity.ok(eventService.getParticipatedEvents(userId)).getBody();
    }
}
