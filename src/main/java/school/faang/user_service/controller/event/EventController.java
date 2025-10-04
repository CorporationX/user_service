package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto eventDto) {
        eventService.create(eventDto);
        return eventDto;
    }

    public EventDto update(long eventId, UpdateEventDto newEventDto) {
        return eventService.update(eventId, newEventDto);
    }

    public List<EventDto> getByFilters(EventFilterDto filters) {
        return eventService.getByFilters(filters); // не понял как реализовать логику фильтрации
    }

    public void delete(long eventId) {
        eventService.delete(eventId);
    }
}
