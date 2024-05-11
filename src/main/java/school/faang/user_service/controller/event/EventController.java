package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    public EventDto create(EventDto eventDto) {
        validByTitleStartDateOwner(eventDto);
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
        validByTitleStartDateOwner(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<Event> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    private void validByTitleStartDateOwner(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Событие должно иметь название, и не должно быть пустым.");
        }
        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Событие должно иметь время начала.");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("Событие должно иметь id владельца.");
        }
    }
}