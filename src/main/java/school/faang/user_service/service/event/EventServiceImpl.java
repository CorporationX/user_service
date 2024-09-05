package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.exceptions.InsufficientSkillsException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filters.EventFilter;
import school.faang.user_service.service.event.util.EventUpdater;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    @Override
    public EventDto create(EventDto eventDto) {
        List<Skill> relatedSkills = getEventSkillsFromDbByIds(eventDto.getRelatedSkillsIds());
        User user = loadUserById(eventDto.getOwnerId());

        validateUserSkills(user, relatedSkills);

        Event event = eventMapper.toEvent(eventDto);
        event.setRelatedSkills(relatedSkills);
        event.setOwner(user);

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public EventDto getEvent(Long eventId) {
        return eventMapper.toDto(findById(eventId));
    }

    @Override
    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();

        eventStream = eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilterDto))
                .reduce(eventStream, (stream, filter) -> filter.apply(stream, eventFilterDto), (s1, s2) -> s1);

        return eventMapper.toFilterDto(eventStream.toList());
    }

    @Override
    public EventWithSubscribersDto updateEvent(EventDto eventDto) {
        Event existingEvent = findById(eventDto.getId());

        List<Skill> relatedSkills = getEventSkillsFromDbByIds(eventDto.getRelatedSkillsIds());
        User user = loadUserById(eventDto.getOwnerId());

        validateUserSkills(user, relatedSkills);

        updateEventFields(existingEvent, eventDto, relatedSkills, user);

        Event updatedEvent = eventRepository.save(existingEvent);

        EventWithSubscribersDto updatedEventDto = eventMapper.toEventWithSubscribersDto(updatedEvent);
        updatedEventDto.setSubscribersCount(getFollowersCount(user));
        return updatedEventDto;
    }

    @Override
    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toDto(eventRepository.findAllByUserId(userId));
    }

    @Override
    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toDto(eventRepository.findParticipatedEventsByUserId(userId));
    }

    @Override
    public void deleteEvent(Long eventId) {
        try {
            eventRepository.deleteById(eventId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Событие с ID " + eventId + " не найдено.");
        }
    }

    private void updateEventFields(Event existingEvent, EventDto eventDto, List<Skill> relatedSkills,
                                   User user) {
        EventUpdater eventUpdater = new EventUpdater(existingEvent)
                .withTitle(eventDto.getTitle())
                .withDescription(eventDto.getDescription())
                .withStartDate(eventDto.getStartDate())
                .withEndDate(eventDto.getEndDate())
                .withLocation(eventDto.getLocation())
                .withMaxAttendees(eventDto.getMaxAttendees())
                .withRelatedSkills(relatedSkills)
                .withEventType(eventDto.getType())
                .withEventStatus(eventDto.getStatus())
                .withOwner(user);

        eventUpdater.build();
    }

    private boolean checkUserSkills(User user, List<Skill> requiredSkills) {
        List<Skill> userSkills = Optional.ofNullable(user.getSkills()).orElse(new ArrayList<>());
        return userSkills.containsAll(requiredSkills);
    }

    private List<Skill> getEventSkillsFromDbByIds(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    private User loadUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    private int getFollowersCount(User user) {
        return user.getFollowers().size();
    }

    private Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Событие с ID " + id + " не найдено."));
    }

    private void validateUserSkills(User user, List<Skill> requiredSkills) {
        if (!checkUserSkills(user, requiredSkills)) {
            throw new InsufficientSkillsException(
                    "Пользователь не обладает необходимыми навыками для проведения этого события.");
        }
    }
}
