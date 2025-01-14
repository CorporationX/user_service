package school.faang.user_service.controller.event;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;
    private static final String ID_PATH = "/{id}";
    private static final String USER_ID_PATH = "/{userId}";
    private static final String OWNED_EVENTS_PATH = "/users" + USER_ID_PATH;
    private static final String PARTICIPATED_EVENTS_PATH = "/participation" + USER_ID_PATH;

    @PostMapping
    public ResponseEntity<EventDto> create(@NotNull @RequestBody EventDto event) {
        validateEvent(event);
        return new ResponseEntity<>(eventService.create(event), HttpStatus.CREATED);
    }

    @GetMapping(ID_PATH)
    public ResponseEntity<EventDto> getEvent(@PathVariable long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @DeleteMapping(ID_PATH)
    public ResponseEntity<String> deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Событие успешно удалено!");
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getEventsByFilter(EventFilterDto filter) {
        return ResponseEntity.ok(eventService.getEventsByFilter(filter));
    }

    @PutMapping(ID_PATH)
    public ResponseEntity<EventDto> updateEvent(@PathVariable long id, @NotNull @RequestBody EventDto event) {
        validateEvent(event);
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @GetMapping(OWNED_EVENTS_PATH)
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable long userId) {
        return ResponseEntity.ok(eventService.getOwnedEvents(userId));
    }

    @GetMapping(PARTICIPATED_EVENTS_PATH)
    public ResponseEntity<List<EventDto>> getParticipatedEvents(@PathVariable long userId) {
        return ResponseEntity.ok(eventService.getParticipatedEvents(userId));
    }

    private void validateEvent(EventDto eventDto) {
        eventValidator.validateEvent(eventDto);
        eventValidator.userCanCreateEventBySkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds());
    }
}
