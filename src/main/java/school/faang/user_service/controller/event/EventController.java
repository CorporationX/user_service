package school.faang.user_service.controller.event;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.check.event.EventCheck;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;
    private final EventCheck eventCheck;
    private static final String ID = "/{id}";
    private static final String USER_ID = "/{userId}";
    private static final String OWNED_EVENTS = "/users" + USER_ID;
    private static final String PARTICIPATED_EVENTS = "/participation" + USER_ID;

    @PostMapping()
    public ResponseEntity<EventDto> create(@NotNull @RequestBody EventDto event) {
        eventCheck(event);
        return new ResponseEntity<>(eventService.create(event), HttpStatus.CREATED);
    }

    @GetMapping(ID)
    public ResponseEntity<EventDto> getEvent(@PathVariable long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @DeleteMapping(ID)
    public ResponseEntity<String> deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Событие успешно удалено!");
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getEventsByFilter(EventFilterDto filter) {
        return ResponseEntity.ok(eventService.getEventsByFilter(filter));
    }

    @PutMapping(ID)
    public ResponseEntity<EventDto> updateEvent(@PathVariable long id, @NotNull @RequestBody EventDto event) {
        eventCheck(event);
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @GetMapping(OWNED_EVENTS)
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable long userId) {
        return ResponseEntity.ok(eventService.getOwnedEvents(userId));
    }

    @GetMapping(PARTICIPATED_EVENTS)
    public ResponseEntity<List<EventDto>> getParticipatedEvents(@PathVariable long userId) {
        return ResponseEntity.ok(eventService.getParticipatedEvents(userId));
    }

    private void eventCheck(EventDto eventDto) {
        eventCheck.eventCheck(eventDto);
        eventCheck.userCanCreateEventBySkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds());
    }
}
