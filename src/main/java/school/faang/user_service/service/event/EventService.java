package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.event.filters.EventFilter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        return saveEvent(eventDto);
    }

    @Transactional(readOnly = true)
    public EventDto getEvent(Long eventId) {
        Event event = findEvent(eventId);
        return eventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByFilters(EventFilterDto filter) {
        return eventRepository.findAll()
                .stream()
                .filter(events -> isAllMatch(filter, events))
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        return saveEvent(eventDto);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsOwner(Long userId) {
        List<Event> eventList = eventRepository.findAllByUserId(userId);
        return eventMapper.toDtoList(eventList);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventParticipants(Long userId) {
        List<Event> eventList = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toDtoList(eventList);
    }

    private EventDto saveEvent(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);

        User newEventOwner = userService.getUser(eventDto.getOwnerId());
        event.setOwner(newEventOwner);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new DataValidationException("Event with ID: " + eventId + " not found.")
        );
    }

    private boolean isAllMatch(EventFilterDto filter, Event events) {
        return eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .allMatch(eventFilter -> eventFilter.applyFilter(events, filter));
    }
}
