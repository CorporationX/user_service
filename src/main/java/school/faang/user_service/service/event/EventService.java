package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
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
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;


    public EventDto create(EventDto eventDto) {
        List<Skill> relatedSkills = loadSkillsByIds(eventDto.getRelatedSkillsIds());

        User user = loadUserByIds(eventDto.getOwnerId());
        EventType eventType = EventType.valueOf(eventDto.getType().toUpperCase());
        EventStatus eventStatus = EventStatus.valueOf(eventDto.getStatus().toUpperCase());

        if (!checkUserSkills(user, relatedSkills)) {
            throw new InsufficientSkillsException(
                    "Пользователь не обладает необходимыми навыками для проведения этого события.");
        }

        Event event = eventMapper.toEvent(eventDto);
        event.setRelatedSkills(relatedSkills);
        event.setOwner(user);
        event.setType(eventType);
        event.setStatus(eventStatus);

        return eventMapper.toDto(eventRepository.save(event));
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Событие с ID " + eventId + " не найдено."));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();

        eventStream = eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilterDto))
                .reduce(eventStream, (stream, filter) -> filter.apply(stream, eventFilterDto), (s1, s2) -> s1);

        return eventMapper.toFilterDto(eventStream.toList());
    }

    public EventWithSubscribersDto updateEvent(EventDto eventDto) {
        Event existingEvent = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Событие с ID " + eventDto.getId() + " не найдено."));

        if (existingEvent == null) {
            throw new EntityNotFoundException("Событие с ID " + eventDto.getId() + " не найдено.");
        }

        List<Skill> relatedSkills = loadSkillsByIds(eventDto.getRelatedSkillsIds());
        User user = loadUserByIds(eventDto.getOwnerId());

        if (!checkUserSkills(user, relatedSkills)) {
            throw new InsufficientSkillsException(
                    "Пользователь не обладает необходимыми навыками для проведения этого события.");
        }

        updateEventFields(existingEvent, eventDto, relatedSkills, user);

        int subscribersCount = user.getFollowers().size();

        Event updatedEvent = eventRepository.save(existingEvent);

        EventWithSubscribersDto updatedEventDto = eventMapper.toEventWithSubscribersDto(updatedEvent);
        updatedEventDto.setSubscribersCount(subscribersCount);
        return updatedEventDto;
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toDto(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toDto(eventRepository.findParticipatedEventsByUserId(userId));
    }


    public void deleteEvent(Long eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
        } else {
            throw new EntityNotFoundException("Событие с ID " + eventId + " не найдено.");
        }

    }

    private void updateEventFields(Event existingEvent, EventDto eventDto, List<Skill> relatedSkills, User user) {
        EventUpdater eventUpdater = new EventUpdater(existingEvent)
                .withTitle(eventDto.getTitle())
                .withDescription(eventDto.getDescription())
                .withStartDate(eventDto.getStartDate())
                .withEndDate(eventDto.getEndDate())
                .withLocation(eventDto.getLocation())
                .withMaxAttendees(eventDto.getMaxAttendees())
                .withRelatedSkills(relatedSkills)
                .withOwner(user);

        eventUpdater.build();
    }

    private boolean checkUserSkills(User user, List<Skill> requiredSkills) {
        List<Skill> userSkills = Optional.ofNullable(user.getSkills()).orElse(new ArrayList<>());
        return userSkills.containsAll(requiredSkills);
    }

    private List<Skill> loadSkillsByIds(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    private User loadUserByIds(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
