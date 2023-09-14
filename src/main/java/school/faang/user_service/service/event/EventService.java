package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;
    private final List<EventFilter> filters;

    public EventDto createEvent(EventDto eventDto) {
        eventValidator.checkIfUserHasSkillsRequired(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public EventDto updateEvent(Long id, EventDto eventFormRequest) {
        eventValidator.checkIfUserHasSkillsRequired(eventFormRequest);
        getEventById(id);
        Event event = eventRepository.save(eventMapper.toEntity(eventFormRequest));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> eventStream = StreamSupport.stream(eventRepository.findAll().spliterator(), false);
        return filterEvents(eventStream, filter);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public EventDto getEvent(long id) {
        Event entity = getEventById(id);
        return eventMapper.toDto(entity);
    }

    private List<EventDto> filterEvents(Stream<Event> events, EventFilterDto filter) {
        List<EventFilter> filteredEvents = filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter)).toList();
        for (EventFilter eventFilter : filteredEvents) {
            events = eventFilter.applyFilter(events, filter);
        }
        return events
                .map(eventMapper::toDto)
                .toList();
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Event not found"));
    }

    public List<Event> getAllPastEventsToDelete() {
        return eventRepository.findAllPastEventsToDelete(LocalDateTime.now().withNano(0));
    }

    public void deleteEvents(List<Event> subList) {
        eventRepository.deleteAll(subList);
    }
}