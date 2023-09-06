package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create(EventDto eventDto) {
        return eventService.create(eventDto);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping
    public List<EventDto> getEventsByFilter(@RequestParam(required = false) Long eventId,
                                            @RequestBody EventFilterDto filters) {
        return eventService.getEventsByFilter(eventId, filters);
    }

    @DeleteMapping("{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping
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

    @PostMapping("{id}/start")
    public EventStartDto startEvent(@PathVariable Long id) {
        log.info("Received start event request for event with id: {}", id);
        return eventService.startEvent(id);
    }
}