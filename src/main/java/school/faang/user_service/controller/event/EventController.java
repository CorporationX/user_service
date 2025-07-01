package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventCreateRequestDto;
import school.faang.user_service.dto.event.EventFilterRequestDto;
import school.faang.user_service.dto.event.EventLiteResponseDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.EventUpdateRequestDto;
import school.faang.user_service.facade.event.EventFacade;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventFacade eventFacade;

    @PostMapping
    public ResponseEntity<EventLiteResponseDto> createEvent(@Valid @RequestBody EventCreateRequestDto dto) {
        EventLiteResponseDto created = eventFacade.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping
    public ResponseEntity<EventLiteResponseDto> updateEvent(@Valid @RequestBody EventUpdateRequestDto dto) {
        EventLiteResponseDto updated = eventFacade.updateEvent(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable long eventId) {
        EventResponseDto event = eventFacade.getEventById(eventId);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable long eventId) {
        eventFacade.deleteEventById(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owned/{userId}")
    public ResponseEntity<List<EventResponseDto>> getOwnedEvents(@PathVariable long userId) {
        List<EventResponseDto> events = eventFacade.getEventsByOwned(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/participated/{userId}")
    public ResponseEntity<List<EventResponseDto>> getParticipatedEvents(@PathVariable long userId) {
        List<EventResponseDto> events = eventFacade.getParticipated(userId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<EventLiteResponseDto>> getEventByFilter(@RequestBody EventFilterRequestDto filter) {
        List<EventLiteResponseDto> events = eventFacade.filter(filter);
        return ResponseEntity.ok(events);
    }
}
