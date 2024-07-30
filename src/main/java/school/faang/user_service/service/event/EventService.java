package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor

public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters; //= List.of(new EventByOwnerFilter(), new EventTitleFilter());
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        eventValidator.inputDataValidation(eventDto);
        Event eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public EventDto getEventById(long eventId) {
        eventValidator.eventValidation(eventId);
        Optional<Event> event = eventRepository.findById(eventId);
        return eventMapper.toDto(event.orElse(null));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();

        return eventFilters.stream().filter(filter ->filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(events, filters))
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(Long eventId) {
        eventValidator.eventValidation(eventId);
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(Long eventId, EventDto eventDto) {
        eventValidator.eventValidation(eventId);
        eventValidator.inputDataValidation(eventDto);
        Event eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Событие id" + eventId + " не найдено"));
        eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toDtoList(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toDtoList(eventRepository.findParticipatedEventsByUserId(userId));
    }
}