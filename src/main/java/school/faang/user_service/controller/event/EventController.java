package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.impl.event.EventService;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(EventDto event) {
        validationEventDto(event);
        return eventService.create(event);
    }

    public EventDto getEvent(long id) {
        return eventService.getEvent(id);

    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);

    }

    public void deleteEvent(long id) {
        eventService.deleteEvent(id);
    }

    public EventDto updateEvent(EventDto eventDto) {
        validationEventDto(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }

    public void validationEventDto(EventDto eventDto) {
        if (eventDto.title() == null || eventDto.title().isBlank()) {
            throw new DataValidationException("Недопустимые значение " + eventDto.title());
        }
        if (eventDto.startDate() == null || eventDto.startDate().isBefore(LocalDate.now().atStartOfDay())) {
            throw new DataValidationException("Недопустимые значение " + eventDto.startDate());
        }
        if (eventDto.ownerId() == null && eventDto.id() == null) {
            throw new DataValidationException("Недопустимые значение Id " );
        }
    }
}