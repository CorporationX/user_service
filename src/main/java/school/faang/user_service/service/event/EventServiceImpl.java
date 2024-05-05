package school.faang.user_service.service.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.event.NotFoundException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventValidator validator;
    private final EventRepository eventRepository;
    private final EventFilterService eventFilterService;
    private final EventMapper mapper;

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(mapper::toDto)
                .toList();
    }

    public EventDto updateEvent(@NonNull EventDto event) {
        validator.validate(event);
        validator.validateOwnersRequiredSkills(event);

        Event eventEntity = mapper.toEntity(event);
        Event saved = eventRepository.save(eventEntity);
        return mapper.toDto(saved);
    }

    public EventDto deleteEvent(long eventId) {
        Event eventToDelete = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event with id=" + eventId + " not found"));
        eventRepository.deleteById(eventToDelete.getId());
        return mapper.toDto(eventToDelete);
    }

    public List<EventDto> getEventsByFilter(@NonNull EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        return eventFilterService.apply(events, filters)
                .map(mapper::toDto)
                .toList();
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("event with id=" + eventId + " not found"));
        return mapper.toDto(event);
    }

    public EventDto create(@NonNull EventDto event) {
        validator.validate(event);
        validator.validateOwnersRequiredSkills(event);

        Event eventEntity = mapper.toEntity(event);
        Event saved = eventRepository.save(eventEntity);
        return mapper.toDto(saved);
    }
}
