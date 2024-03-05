package school.faang.user_service.controller.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@RestController
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    public EventDto create(EventDto eventDto) {
        validateEventDto(eventDto);
        return eventService.create(eventDto);
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
