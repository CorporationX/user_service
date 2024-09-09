package school.faang.user_service.controller.event;

import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventServiceImpl;

import java.util.List;

@RestController
public class EventController implements EventControllerApi {

    private final EventServiceImpl eventServiceImpl;
    private final EventMapper eventMapper;

    public EventController(EventServiceImpl eventServiceImpl, EventMapper eventMapper) {
        this.eventServiceImpl = eventServiceImpl;
        this.eventMapper = eventMapper;
    }

    @Override
    public EventDto createEvent(EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        Event createdEvent = eventServiceImpl.create(event);
        return eventMapper.toDto(createdEvent);
    }

    @Override
    public EventWithSubscribersDto updateEvent(EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        Event updatedEvent = eventServiceImpl.updateEvent(event);
        Integer subscribersCount = eventServiceImpl.getSubscribersCount(updatedEvent);
        return eventMapper.toEventWithSubscribersDto(updatedEvent, subscribersCount);
    }

    @Override
    public EventDto getEvent(Long eventId) {
        Event event = eventServiceImpl.getEvent(eventId);
        return eventMapper.toDto(event);
    }

    @Override
    public List<EventDto> getEventsByFilter(EventFilters eventFilters) {
        List<Event> events = eventServiceImpl.getEventsByFilter(eventFilters);
        return eventMapper.toFilteredEventsDto(events);
    }

    @Override
    public List<EventDto> getOwnedEvents(Long userId) {
        List<Event> events = eventServiceImpl.getOwnedEvents(userId);
        return eventMapper.toDto(events);
    }

    @Override
    public List<EventDto> getParticipatedEvents(Long userId) {
        List<Event> events = eventServiceImpl.getParticipatedEvents(userId);
        return eventMapper.toDto(events);
    }

    @Override
    public void deleteEvent(Long eventId) {
        eventServiceImpl.deleteEvent(eventId);
    }
}