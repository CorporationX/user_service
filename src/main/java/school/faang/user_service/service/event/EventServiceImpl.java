package school.faang.user_service.service.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.event.DataValidationException;
import school.faang.user_service.exceptions.event.EventNotFoundException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final List<EventFilter> eventFilters;
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
        if (doesOwnerHasRequiredSkills(event)) {
            Event eventEntity = mapper.toEntity(event);
            Event saved = eventRepository.save(eventEntity);
            return mapper.toDto(saved);
        } else {
            throw new DataValidationException("user with id=" + event.getOwnerId() + " has no enough skills to update event");
        }
    }

    public EventDto deleteEvent(long eventId) {
        Event eventToDelete = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("cannot find event with id=" + eventId));
        eventRepository.deleteById(eventToDelete.getId());
        return mapper.toDto(eventToDelete);
    }

    public List<EventDto> getEventsByFilter(@NonNull EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        return eventFilters.stream()
                .filter(filter -> filter.isAcceptable(filters))
                .flatMap(filter -> filter.apply(events, filters))
                .map(mapper::toDto)
                .toList();
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("cannot find event with id=" + eventId));
        return mapper.toDto(event);
    }

    public EventDto create(@NonNull EventDto event) {
        if (doesOwnerHasRequiredSkills(event)) {
            Event eventEntity = mapper.toEntity(event);
            Event saved = eventRepository.save(eventEntity);
            return mapper.toDto(saved);
        } else {
            throw new DataValidationException("user with id=" + event.getOwnerId() + " has no enough skills to create event");
        }
    }

    private boolean doesOwnerHasRequiredSkills(EventDto event) {
        User user = userRepository
                .findById(event.getOwnerId())
                .orElseThrow(() -> new DataValidationException("owner with id=" + event.getOwnerId() + " not found"));

        Set<Long> requiredSkillsIds = user.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        return event.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .allMatch(requiredSkillsIds::contains);
    }
}
