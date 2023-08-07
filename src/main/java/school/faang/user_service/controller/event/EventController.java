package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/event")
    public EventDto create(EventDto eventDto) {
        return eventService.create(eventDto);
    }

    @GetMapping("/event/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @GetMapping("/event")
    public List<EventDto> getEventsByFilter(@RequestParam(required = false) Long eventId,
                                            @RequestBody EventFilterDto filters) {
        return eventService.getEventsByFilter(eventId, filters);
    }

    @DeleteMapping("/event/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping("/event")
    public EventDto updateEvent(@RequestBody EventDto eventDto) {
        return eventService.updateEvent(eventDto);
    }

    @GetMapping("/owned-event/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/participated-event/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}