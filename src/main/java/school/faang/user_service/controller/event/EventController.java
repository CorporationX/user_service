package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    // Создать событие
    @PostMapping("")
    public ResponseEntity<EventDto> create(@RequestBody EventDto event) {
        if (validateEventDto(event)) {
            return ResponseEntity.ok(eventService.create(event));
        }
        throw new DataValidationException("Не удалось создать событие!" +
                                          " Введены не верные данные.");
    }

    // получить событие
    @GetMapping("/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable("id") long eventId) {
        return ResponseEntity.ok(eventService.getEvent(eventId));
    }

    public boolean validateEventDto(EventDto event) {
        return !event.getTitle().isEmpty()
               && !event.getTitle().isBlank()
               && event.getTitle().length() <= 64
               && event.getStartDate() != null
               && event.getOwnerId() != 0;
    }

    //Получить все события с фильтрами
    @PostMapping("/filtered")
    public ResponseEntity<List<EventDto>> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return ResponseEntity.ok(eventService.getEventsByFilter(filter));
    }

    // Удалить событие
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") long idEvent) {
        eventService.deleteEvent(idEvent);
        return ResponseEntity.ok().build();
    }

    //Обновить событие
    @PatchMapping("")
    public ResponseEntity<EventDto> updateEvent(@RequestBody EventDto event) {
        if (event == null) {
            throw new DataValidationException("Событие не передано!");
        }
        return ResponseEntity.ok(eventService.updateEvent(event));
    }

    // Получить все созданные пользователем события
    @GetMapping("/created/all")
    public ResponseEntity<List<EventDto>> getOwnedEvents(@RequestParam("userId") long userId) {
        return ResponseEntity.ok(eventService.getOwnedEvents(userId));
    }

    // Получить все события, в которых пользователь принимает участие
    @GetMapping("/participated/all")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(
            @RequestParam("userId") long userId) {
        return ResponseEntity.ok(eventService.getParticipatedEvents(userId));
    }
}
