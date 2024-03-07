package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto eventDto) {
        validateEventDto(eventDto);
        return eventService.create(eventDto);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        return eventService.getEventsByFilter(filters);
    }

    public void deleteEvent(long eventId){
        eventService.deleteEvent(eventId);
    }

    public void updateEvent(EventDto eventDto) {
        validateEventDto(eventDto);
        eventService.updateEvent(eventDto);
    }

    private void validateEventDto(EventDto eventDto) throws DataValidationException {
        if (eventDto.getTitle() == null || eventDto.getTitle().isEmpty() || eventDto.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Event title can't be empty");
        }
        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Specify the start date of the event");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidationException("Enter the owner of the event");
        }
    }
}
