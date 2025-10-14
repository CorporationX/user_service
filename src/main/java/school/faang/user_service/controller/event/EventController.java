package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
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

    public EventDto create(@Valid EventDto eventDto) {
        return eventService.create(eventDto);
    }

    public EventDto update(long eventId, @Valid UpdateEventDto newEventDto) {
        return eventService.update(eventId, newEventDto);
    }

    public List<EventDto> getByFilters(@Valid EventFilterDto filters) {
        return eventService.getByFilters(filters);
    }

    public void delete(long eventId) {
        eventService.delete(eventId);
    }
}
