package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventValidator eventValidator;

    public EventDto create(@Valid @RequestBody EventDto event) {
        eventValidator.validateStartDate(event);
        return eventService.create(event);
    }

    public EventDto getEvent(Long id) {
        return eventService.getEvent(id);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(Long id) {
        eventService.deleteEvent(id);
    }

    public EventDto updateEvent(EventDto event) {
        eventValidator.validateTitlePresent(event);
        eventValidator.validateStartDate(event);
        eventValidator.validateOwnerPresent(event);

        return eventService.updateEvent(event);
    }

    public List<Event> getOwnedEvents(Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<Event> getParticipatedEvents(Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
