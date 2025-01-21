package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventFilterDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    public EventDto create(EventDto eventDto) {
        log.info("Received request to create a new event: {}", eventDto);

        validateEvent(eventDto);
        return eventService.create(eventDto);
    }

    public EventDto getEvent(long eventId) {
        log.info("Received request to get event with id: {}", eventId);

        return eventService.getEvent(eventId);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        log.info("Received request to get events by filter: {}", filter);

        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        log.info("Received request to delete event with id: {}", eventId);

        eventService.deleteEvent(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        log.info("Received request to update event: {}", eventDto);

        validateEvent(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        log.info("Received request to get events owned by user with id: {}", userId);

        return eventService.getOwnedEvents(userId);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        log.info("Received request to get events participated by user with id: {}", userId);

        return eventService.getParticipatedEvents(userId);
    }

    private void validateEvent(EventDto event) {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new DataValidationException("Event's title cannot be empty");
        }
        if (event.getStartDate() == null) {
            throw new DataValidationException("Event start date is required");
        }
        if (event.getOwnerId() == null) {
            throw new DataValidationException("Event owner is required");
        }
    }

}
