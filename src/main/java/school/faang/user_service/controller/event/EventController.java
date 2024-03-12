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
    private final EventService eventService;
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) throws Exception {
        eventValidator.validateEventInController(eventDto);
        return eventService.createEvent(eventDto);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        return eventService.getEventsByFilter(eventFilterDto);
    }

    public void deleteEvent(Long eventId) {
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateEventInController(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getOwnedEvents(Long ownerId){
        return eventService.getOwnedEvents(ownerId);
    }

    public List<EventDto> getParticipatedEvents(Long userId){
        return eventService.getParticipatedEvents(userId);
    }
}
