package school.faang.user_service.controller.event;


import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping("/event")
    public EventDto createEvent(@RequestBody EventDto eventDto) {
        validateEvent(eventDto);
        return eventService.createEvent(eventDto);
    }

    public void validateEvent(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }

        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Event start date cannot be null");
        }

        if (eventDto.getOwnerId() == null || eventDto.getOwnerId() < 0) {
            throw new DataValidationException("Event owner ID cannot be null");
        }
    }
}
