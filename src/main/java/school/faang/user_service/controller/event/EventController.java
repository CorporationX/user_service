package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
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

    @GetMapping("/events")
    public List<EventDto> getEventsByFilter(@RequestParam(required = false) Long eventId,
                                            @RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(eventId, filter);
    }
}