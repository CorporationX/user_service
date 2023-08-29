package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Управление событиями")
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

    @Operation(summary = "Добавление события")
    @PostMapping("/")
    public EventDto create(@RequestBody EventDto event) {
        validateEvent(event);
        return eventService.create(event);
    }

    @Operation(summary = "Удаление события по идентификатору")
    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
    }

    @Operation(summary = "Получить список событий для участника по идентификатору")
    @GetMapping("/participants/{userId}")
    public List<EventDto> getParticipationEvents(@PathVariable Long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    @Operation(summary = "Получить список событий для организатора по идентификатору")
    @GetMapping("/owner/{ownerId}")
    public List<EventDto> getOwnedEvents(@PathVariable Long ownerId) {
        return eventService.getOwnedEvents(ownerId);
    }

    @Operation(summary = "Получить список событий по фильтру")
    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @Operation(summary = "Получить событие по идентификатору")
    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.get(id);
    }

    @Operation(summary = "Обновить событие")
    @PutMapping("/")
    public EventDto updateEvent(@RequestBody EventDto event) {
        validateEvent(event);
        return eventService.updateEvent(event);
    }

    @Operation(summary = "Начать событие")
    @PutMapping("/{id}")
    public EventDto startEvent(@PathVariable Long id) {
        return eventService.startEvent(id);
    }
}
