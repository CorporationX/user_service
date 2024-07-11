package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.Exceptions;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.filter.EventFilter;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validation.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final Validator validator;
    private final Exceptions exception;
    private final SkillService skillService;

    @Setter
    private List<EventFilter> filters;

    public EventDto create(EventDto eventDto) {
        validator.checkEventDto(eventDto);
        Event oldEvent = eventMapper.toEntity(eventDto);
        Event newEvent = eventRepository.save(oldEvent);

        return eventMapper.toDto(newEvent);
    }

    public EventDto getEvent(Long id) {
        validator.checkLongFieldIsNullAndZero(id);
        Optional<Event> eventEntity = findEventEntity(id);

        return eventMapper.toDto(eventEntity.get());
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> allEvents = eventRepository.findAll();

        return filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .flatMap(eventFilter -> eventFilter.apply(allEvents, filter))
                .distinct()
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(Long id) {
        validator.checkLongFieldIsNullAndZero(id);
        Optional<Event> eventEntity = findEventEntity(id);
        eventRepository.deleteById(eventEntity.get().getId());
    }

    public EventDto updateEvent(EventDto event) {
        ownerHasEventRelatedSkills(event);
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(event)));
    }

    public List<Event> getOwnedEvents(long userId) {
        return Optional.ofNullable(eventRepository.findAllByUserId(userId)).
                orElseThrow(exception::notGetOwnedEvents);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return Optional.ofNullable(eventRepository.findParticipatedEventsByUserId(userId)).
                orElseThrow(exception::notGetParticipatedEvents);
    }

    private Optional<Event> findEventEntity(Long id) {
        return Optional.ofNullable(eventRepository.findById(id)).orElseThrow(exception::findByIdIsNull);
    }

    private void ownerHasEventRelatedSkills(EventDto event) {
        Set<SkillDto> ownerSkills = new HashSet<>(skillService.getUserSkills(event.getOwnerId()));

        if (!ownerSkills.containsAll(event.getRelatedSkills())) {
            exception.cantGetUserSkills();
        }
    }
}
