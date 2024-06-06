package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.EventValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        eventValidator.validByTitleStartDateOwner(eventDto);
        return eventService.create(eventDto);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validByTitleStartDateOwner(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<Event> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}