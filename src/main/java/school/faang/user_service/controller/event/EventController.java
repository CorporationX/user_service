package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    //??? добавил базовый URL-шаблон для всех обработчиков запросов @RequestMapping("/v1/events"), чтоб сократить код,
    //но теперь это не сосем сематически правильно, например мы создаем событие (одно, т.е event) по "/v1/events",
    //нужно это исправить и явно везде прописать? или я сейчас сделал правильно?

    @PostMapping
    public EventDto create(@RequestBody EventDto event) {
        return eventService.create(event);
    }

    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable long eventId) {
        return eventService.getEvent(eventId);
    }

    @GetMapping
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("/{eventId}")
    public void deleteEvent(@PathVariable long eventId) {
        eventService.deleteEvent(eventId);
    }

    @PutMapping
    public EventDto updateEvent(@RequestBody EventDto event) {
        return eventService.updateEvent(event);
    }

    @GetMapping("/owned/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participated/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
