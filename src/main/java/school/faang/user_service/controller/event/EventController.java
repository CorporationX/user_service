package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.event.EventValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        eventValidator.validateEventDtoFields(eventDto);
        return eventService.create(eventDto);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        return eventService.getEventsByFilter(filters);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public void updateEvent(EventDto eventDto) {
        eventValidator.validateEventDtoFields(eventDto);
        eventService.updateEvent(eventDto);
    }
}
