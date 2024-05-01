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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper mapper;

    public EventDto updateEvent(@NonNull EventDto event) {
        if (hasOwnerEnoughSkillsForEvent(event)) {
            Event eventEntity = mapper.toEntity(event);
            Event saved = eventRepository.save(eventEntity);
            return mapper.toDto(saved);
        } else {
            throw new DataValidationException(String.format("user with id=%d has no enough skills to update event", event.getOwnerId()));
        }
    }

    public EventDto deleteEvent(long eventId) {
        Event eventToDelete = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("cannot find event with id=%d", eventId)));
        eventRepository.deleteById(eventToDelete.getId());
        return mapper.toDto(eventToDelete);
    }

    public List<EventDto> getEventsByFilter(@NonNull EventFilterDto filter) {
        return eventRepository.findAll().stream()
                .filter(filter.toPredicate())
                .map(mapper::toDto)
                .toList();
    }

    public EventDto getEvent(long eventId) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(String.format("cannot find event with id=%d", eventId)));
        return mapper.toDto(event);
    }

    public EventDto create(@NonNull EventDto event) {
        if (hasOwnerEnoughSkillsForEvent(event)) {
            Event eventEntity = mapper.toEntity(event);
            Event saved = eventRepository.save(eventEntity);
            return mapper.toDto(saved);
        } else {
            throw new DataValidationException(String.format("user with id=%d has no enough skills to create event", event.getOwnerId()));
        }
    }

    private boolean hasOwnerEnoughSkillsForEvent(EventDto event) {
        User user = userRepository
                .findById(event.getOwnerId())
                .orElseThrow(() -> new DataValidationException(String.format("owner with id=%d not found", event.getOwnerId())));
        Set<Long> requiredSkillsIds = user.getSkills().stream().map(Skill::getId).collect(Collectors.toSet());
        return event.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .allMatch(requiredSkillsIds::contains);
    }
}
