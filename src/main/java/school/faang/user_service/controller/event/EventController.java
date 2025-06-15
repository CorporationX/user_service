package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public EventDto create(@RequestBody EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    private void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Название события не может быть пустым.");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("Дата начала события обязательна.");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("ID пользователя, проводящего событие, обязателен.");
        }
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @GetMapping("/filter")
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping
    public EventDto updateEvent(@RequestBody EventDto event) {
        validateEvent(event);
        return eventService.updateEvent(event);
    }

    @GetMapping("/owned")
    public List<EventDto> getOwnedEvents(@RequestParam long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participated")
    public List<EventDto> getParticipatedEvents(@RequestParam long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
