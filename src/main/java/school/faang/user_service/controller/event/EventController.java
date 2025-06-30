package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@RequestBody @Validated EventDto event) {
        return eventService.create(event);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long id, @RequestBody @Valid EventDto eventDto) {
        eventDto.setId(id);
        EventDto updatedEvent = eventService.updateEvent(eventDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @GetMapping("/owned/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participated")
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
