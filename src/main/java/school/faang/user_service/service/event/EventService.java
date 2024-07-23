package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.filter.EventByOwnerFilter;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventTitleFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor

public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final SkillMapper skillMapper;
    private final List<EventFilter> eventFilters = List.of(new EventByOwnerFilter(), new EventTitleFilter());
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        eventValidator.inputDataValidation(eventDto);
        Event eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public EventDto getEventById(long eventId) {
        eventValidator.eventValidation(eventId);
        Event event = eventRepository.getById(eventId);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        for (EventFilter filter : eventFilters) {
            if (filter.isApplicable(filters)) {
                events = filter.apply(events, filters);
            }
        }
        return eventMapper.toDtoList(events.collect(Collectors.toList()));
    }

    public void deleteEvent(Long eventId) {
        eventValidator.eventValidation(eventId);
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(Long eventId, EventDto eventDto) {
        eventValidator.eventValidation(eventId);
        eventValidator.inputDataValidation(eventDto);
        Event eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Событие не найдено"));
        eventEntity.setId(eventDto.getId());
        eventEntity.setTitle(eventDto.getTitle());
        eventEntity.setStartDate(eventDto.getStartDate());
        eventEntity.setEndDate(eventDto.getEndDate());
        eventEntity.setDescription(eventDto.getDescription());
        eventEntity.setRelatedSkills(skillMapper.toEntityList(eventDto.getRelatedSkills()));
        eventEntity.setLocation(eventDto.getLocation());
        eventEntity.setMaxAttendees(eventDto.getMaxAttendees());
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toDtoList(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toDtoList(eventRepository.findParticipatedEventsByUserId(userId));
    }
}