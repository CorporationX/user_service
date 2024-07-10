package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    public EventDto create(EventDto eventDto) {
        validate(eventDto);
        return eventService.create(eventDto);
    }

    private void validate(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("title can't be null or empty");
        } else if (eventDto.getStartDate() == null) {
            throw new DataValidationException("getStartDate can't be null");
        } else if (eventDto.getOwnerId() == 0) {
            throw new DataValidationException("ownerId can't be 0");
        }
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
        validate(eventDto);
        return eventService.updateEvent(eventDto);
    }


}
