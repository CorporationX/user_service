package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    // Создать событие
    public EventDto create(EventDto event) {
        if (validateEventDto(event)) {
            return eventService.create(event);
        }
        throw new DataValidationException("Не удалось создать событие!" +
                " Введены не верные данные.");
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

    //Получить все события с фильтрами
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    // Удалить событие
    public void deleteEvent(long idEvent) {
        eventService.deleteEvent(idEvent);
    }

     //Обновить событие
    public EventDto updateEvent(EventDto event) {
        if (event.getTitle().isBlank()
                || event.getStartDate().toString().isBlank()
                || event.getOwnerId() == null) {
            throw new DataValidationException("Событие не прошло согласование!");
        }
        return eventService.updateEvent(event);
    }

    // Получить все созданные пользователем события
    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    // Получить все события, в которых пользователь принимает участие
    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
