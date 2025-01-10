package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto createEvent(@Valid @RequestBody EventDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable @Positive Long eventId) throws DataValidationException {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/filter")
    public List<EventDto> filterEvents(@Valid @RequestBody EventFilterDto filterDto) {
        return eventService.getEventsByFilters(filterDto);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable @Positive Long eventId) throws DataValidationException {
        eventService.deleteEvent(eventId);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEvent(
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody EventDto eventDto) throws DataValidationException {
        eventDto.setId(eventId);
        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/owner/{userId}")
    public List<EventDto> getEventsByOwner(@PathVariable @Positive Long userId) {
        return eventService.getEventsByOwner(userId);
    }

    @GetMapping("/participant/{userId}")
    public List<EventDto> getEventsByParticipant(@PathVariable @Positive Long userId) {
        return eventService.getEventsByParticipant(userId);
    }
}
