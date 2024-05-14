package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventValidator eventValidator;
    private final EventService eventService;

    public EventDto create(EventDto eventDto) {
        eventValidator.validateEvent(eventDto);
        return eventService.create(eventDto);
    }

    public EventDto getEventDto(long eventId) {
        return eventService.getEventDto(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        return eventService.getEventsByFilter(filterDto);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateEvent(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
