package school.faang.user_service.controller.event;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.check.event.EventCheck;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    private final EventCheck eventCheck;

    @PostMapping("/create")
    public ResponseEntity<EventDto> create(@NotNull @RequestBody EventDto event) {
        eventCheck(event);
        return ResponseEntity.ok(eventService.create(event));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Событие успешно удалено!");
    }

    @GetMapping
    public ResponseEntity<List<EventDto>> getEventsByFilter(@NotNull EventFilterDto filter) {
        return ResponseEntity.ok(eventService.getEventsByFilter(filter));
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable long id, @NotNull @RequestBody EventDto event) {
        eventCheck(event);
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @GetMapping("/by_userid/{userId}")
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable long userId) {
        return ResponseEntity.ok(eventService.getOwnedEvents(userId));
    }

    @GetMapping("/participation/{userId}")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(@PathVariable long userId) {
        return ResponseEntity.ok(eventService.getParticipatedEvents(userId));
    }

    private void eventCheck(EventDto eventDto) {
        eventCheck.eventCheck(eventDto);
        eventCheck.userCanCreateEventBySkills(eventDto.getOwnerId(), eventDto.getRelatedSkillIds());
    }
}
