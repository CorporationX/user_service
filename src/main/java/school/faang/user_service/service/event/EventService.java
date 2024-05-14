package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        eventValidator.checkOwnerSkills(eventDto);
        Event eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public EventDto getEventDto(long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        Event event = optionalEvent.orElseThrow(() -> new EntityNotFoundException("Event with Id " + eventId + " not found."));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        Stream<Event> events = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(filterDto)) {
                events = eventFilter.apply(events, filterDto);
            }
        }
        return eventMapper.toListDto(events.collect(Collectors.toList()));
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Long eventDtoId = eventDto.getId();
        Event event = eventRepository.findById(eventDtoId)
                .orElseThrow(() -> new EntityNotFoundException("Event with Id " + eventDtoId + " not found"));
        eventValidator.checkOwnerSkills(eventDto);

        return eventMapper.toDto(eventRepository.save(event));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEventsByUserId = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toListDto(participatedEventsByUserId);
    }
}
