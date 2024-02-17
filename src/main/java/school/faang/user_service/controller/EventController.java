package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.EventService;
import school.faang.user_service.validator.EventValidator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        eventValidator.checkFilterNotNull(filterDto);
        return eventService.getEventsByFilter(filterDto);
    }

    @PostMapping("/create")
    public EventDto create(@RequestBody EventDto eventDto) {
        eventValidator.validateEventInController(eventDto);
        return eventService.create(eventDto);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateEventInController(eventDto);
        return eventService.updateEvent(eventDto);
    }

    public List<EventDto> getParticipatedEventsByUserId(long userId) {
        return eventService.getParticipatedEventsByUserId(userId);
    }

    public EventDto getEvent(long eventId) {
        return eventService.getEventDto(eventId);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEventById(eventId);
    }

}