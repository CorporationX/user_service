package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilterDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        if (!eventValidator.validateEventDto(eventDto)) {
            throw new DataValidationException("Не валидные данные в EventDto");
        }
        return eventService.create(eventDto);
    }

    public EventDto getEvent(Long eventId) {
        if (!eventValidator.validateId(eventId)) {
            throw new DataValidationException("Не валидный id");
        }
        return eventService.getEvent(eventId);
    }

    public void deleteEvent(Long eventId) {
        if (!eventValidator.validateId(eventId)) {
            throw new DataValidationException("Не валидный id");
        }
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        if (!eventValidator.validateEventDto(eventDto)) {
            throw new DataValidationException("Не валидные данные в EventDto");
        }
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        if (!eventValidator.validateId(userId)) {
            throw new DataValidationException("Не валидный id");
        }
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        if (!eventValidator.validateId(userId)) {
            throw new DataValidationException("Не валидный id");
        }
        return eventService.getParticipatedEvents(userId);
    }

}
