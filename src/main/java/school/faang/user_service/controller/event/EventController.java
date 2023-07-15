package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventDto create(EventDto event) {
        validate(event);
        return eventMapper.toDTO(eventService.create(event));
    }

    private void validate(EventDto event) {
        if (event == null || event.getTitle() == null || event.getTitle().isBlank() || event.getStartDate() == null || event.getOwnerId() == null) {
            throw new DataValidationException("Event is invalid");
        }
    }

    public EventDto getEvent(long id){
        return eventMapper.toDTO(eventService.getEvent(id));
    }
}
