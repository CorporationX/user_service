package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final List<EventFilter> eventFilters;
    private final UserRepository userRepository;

    @Transactional
    public EventDto create(EventDto event) {
        eventValidator.validateOwnerSkills(event);
        Event eventEntity = eventMapper.toEntity(event);
        EventDto newEvent = eventMapper.toDto(eventRepository.save(eventEntity));
        log.info("Event created: {}", newEvent.id());
        return newEvent;
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found for ID: " + eventId));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();
        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(filterDto)) {
                eventStream = eventFilter.apply(eventStream, filterDto);
            }
        }
        return eventMapper.toListDto(eventStream.toList());
    }

    public void deleteEvent(Long eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
            log.info("Event deleted: " + eventId);
        } else {
            log.warn("Event not found for ID: " + eventId);
        }
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateOwnerSkills(eventDto);

        Event eventEntity = eventMapper.toEntity(eventDto);
        Event saved = eventRepository.save(eventEntity);

        return eventMapper.toDto(saved);
    }

    public List<Event> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public List<Event> getParticipatedEvents(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }
}
