package school.faang.user_service.controller.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    public EventDto create(@NonNull EventDto eventDto) {
        if (isInvalidToCreate(eventDto)) {
            throw new DataValidationException("У создаваемого события не хватает входящих данных!");
        }
        return eventService.create(eventDto);
    }

    public EventDto getEvent(@NonNull Long eventId) {
        if (isIdInvalid(eventId)) {
            throw new DataValidationException("Передан неверный ID события для получения данных");
        }
        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(@NonNull EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(@NonNull Long eventId) {
        if (isIdInvalid(eventId)) {
            throw new DataValidationException("Передан неверный ID события для удаления");
        }
        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(@NonNull EventDto eventDto) {
        if (isInvalidToCreate(eventDto)) {
            throw new DataValidationException("У обновляемого события не хватает входящих данных!");
        }
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getOwnedEvents(@NonNull Long userId) {
        if (isIdInvalid(userId)) {
            throw new DataValidationException("Передан неверный ID для получения событий, созданных пользователем");
        }
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(@NonNull Long userId) {
        if (isIdInvalid(userId)) {
            throw new DataValidationException("Передан неверный ID для получения событий, созданных пользователем");
        }
        return eventService.getParticipatedEvents(userId);
    }

    private boolean isInvalidToCreate(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()
                || eventDto.getStartDate() == null || eventDto.getOwnerId() == null) {
            log.warn("Не пройдена валидация события для создания. Проверьте: название, дату начала и владельца");
            return true;
        }
        return false;
    }

    private boolean isIdInvalid(Long id) {
        if (id <= 0) {
            log.warn("Передан неположительный userId");
            return true;
        }
        return false;
    }
}
