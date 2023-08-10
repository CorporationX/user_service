package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/events")
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create(@RequestBody EventDto event) {
        log.info("POST endpoint 'create()' was called successfully");
        validate(event);
        return eventService.create(event);
    }

    private void validate(EventDto event) {
        if (event == null) {
            throw new DataValidationException("Event can't be null");
        } else if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new DataValidationException("Event's title can't be null or blank");
        } else if (event.getStartDate() == null) {
            throw new DataValidationException("Event's start date can't be null");
        } else if (event.getOwnerId() == null) {
            throw new DataValidationException("Event's ownerId can't be null");
        }
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable("eventId") long id) {
        log.info("GET endpoint 'getEvent()' was called successfully with eventId {}", id);
        return eventService.getEvent(id);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable("eventId") long id) {
        log.info("DELETE endpoint 'deleteEvent()' was called successfully with eventId {}", id);
        eventService.deleteEvent(id);
    }

    @PutMapping
    public EventDto updateEvent(@RequestBody EventDto event) {
        log.info("PUT endpoint 'updateEvent()' was called successfully");
        validate(event);
        return eventService.updateEvent(event);
    }

    @GetMapping("/owner/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable("userId") long userId) {
        log.info("GET endpoint 'getOwnedEvents()' was called successfully with userId {}", userId);
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participant/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") long userId) {
        log.info("GET endpoint 'getParticipatedEvents()' was called successfully with userId {}", userId);
        return eventService.getParticipatedEvents(userId);
    }

    @GetMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        log.info("GET endpoint 'getEventsByFilter()' was called successfully");
        return eventService.getEventsByFilter(filter);
    }
}
