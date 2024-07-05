package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.service.event.EventService;
import org.apache.*;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        if (validateEventDto(event)) {
            return eventService.create(event);
        } else {
            throw new DataValidationException("Не удалось создать событие!" +
                    " Введены не верные данные.");
        }
    }

    // получить событие
    public EventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public boolean validateEventDto(EventDto event) {
        return !event.getTitle().isEmpty()
                && !event.getTitle().isBlank()
                && event.getTitle().length() <= 64
                && event.getStartDate() != null
                && event.getOwnerId() != 0;
    }
}
