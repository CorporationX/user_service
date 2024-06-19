package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RequestMapping("events")
@RestController
@AllArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventControllerValidation eventControllerValidation;

    @PostMapping
    public EventDto create(@Valid @RequestBody EventDto event) {
        eventControllerValidation.validateEventDates(event);

        return eventService.create(event);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@NotNull @RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping
    public EventDto update(@RequestBody EventDto event) {
        eventControllerValidation.validateEventId(event);
        eventControllerValidation.validateEventDates(event);

        return eventService.updateEvent(event);
    }

    @GetMapping
    public List<EventDto> getEvents(@NotNull @RequestParam Long userId, @RequestParam boolean isOwner) {
        if (isOwner) {
            return eventService.getOwnedEvents(userId);
        }
        return eventService.getParticipatedEvents(userId);
    }
}
