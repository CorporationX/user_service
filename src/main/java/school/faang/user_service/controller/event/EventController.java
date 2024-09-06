package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventServiceImpl eventServiceImpl;
    private final EventMapper eventMapper;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@Valid @RequestBody EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        Event createdEvent = eventServiceImpl.create(event);
        return eventMapper.toDto(createdEvent);
    }

    @PatchMapping()
    public EventWithSubscribersDto updateEvent(@Valid @RequestBody EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        Event updatedEvent = eventServiceImpl.updateEvent(event);
        return eventMapper.toEventWithSubscribersDto(updatedEvent, eventServiceImpl.getSubscribersCount(updatedEvent));
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable("id") Long eventId) {
        Event event = eventServiceImpl.getEvent(eventId);
        return eventMapper.toDto(event);
    }

    @PostMapping("/filter")
    public List<EventDto> getEventsByFilter(@RequestBody EventFilters eventFilters) {
        List<Event> events = eventServiceImpl.getEventsByFilter(eventFilters);
        return eventMapper.toFilteredEventsDto(events);
    }

    @GetMapping("/owned-events/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable("userId") Long userId) {
        List<Event> events = eventServiceImpl.getOwnedEvents(userId);
        return eventMapper.toDto(events);
    }


    @GetMapping("/participated-events/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") Long userId) {
        List<Event> events = eventServiceImpl.getParticipatedEvents(userId);
        return eventMapper.toDto(events);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable("id") Long eventId) {
        eventServiceImpl.deleteEvent(eventId);
    }
}
