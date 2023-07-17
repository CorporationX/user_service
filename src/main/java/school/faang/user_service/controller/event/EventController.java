package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventService;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto eventDto) {
        return eventService.create(eventDto);
    }

    public EventDto get(Long eventId) {
        return eventService.get(eventId);
    }

    public boolean deleteEvent(Long eventId) {
        return eventService.deleteEvent(eventId);
    }
}