package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private static final int MIN_NAME_LENGTH = 3;

    private void validateTitle(String name) {
        if (name == null || name.length() < MIN_NAME_LENGTH) {
            throw new DataValidationException("Name is required and should be at least 3 symbols");
        }
    }

    private void validateStartDate(LocalDateTime startDate) {
        if (startDate == null) {
            throw new DataValidationException("Start date is required");
        }
    }

    private static void validateUserId(Long ownerId) {
        if (ownerId == null) {
            throw new DataValidationException("User id is required");
        }
    }

    private void validateEvent(EventDto event) {
        validateTitle(event.getTitle());
        validateStartDate(event.getStartDate());
        validateUserId(event.getOwnerId());
    }

    @PostMapping("/")
    public EventDto create(@RequestBody EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
    }

    @GetMapping("/participants/{userId}")
    public List<EventDto> getParticipationEvents(@PathVariable Long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @GetMapping("/owner/{ownerId}")
    public List<EventDto> getOwnedEvents(@PathVariable Long ownerId) {
        return eventService.getOwnedEvents(ownerId);
    }

    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.get(id);
    }

    @PutMapping("/")
    public EventDto updateEvent(@RequestBody EventDto event) {
        validateEvent(event);
        return eventService.updateEvent(event);
    }
}
