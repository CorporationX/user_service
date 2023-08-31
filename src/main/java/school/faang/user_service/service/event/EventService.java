package school.faang.user_service.service.event;


import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filters.EventFilter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final UserRepository userRepository;

    private void validateUserAccess(List<Long> skills, Long ownerId) {
        List<Skill> userSkills = skillRepository.findAllByUserId(ownerId);

        Set<Long> eventSkills = new HashSet<>(skills);
        Set<Long> userSkillIds = new HashSet<>(userSkills.stream().map(Skill::getId).toList());

        boolean hasUserPermission = eventSkills.containsAll(userSkillIds);

        if (!hasUserPermission) {
            throw new DataValidationException("User doesn't have access");
        }
    }

    public EventDto create(EventDto event) {
        validateUserAccess(event.getRelatedSkills(), event.getOwnerId());
        List<Skill> skills = skillRepository.findAllById(event.getRelatedSkills());
        User owner = userRepository
            .findById(event.getOwnerId())
            .orElseThrow(() -> new EntityNotFoundException("User with id: " + event.getOwnerId() + " is not exist"));

        Event newEvent = eventMapper.toEntity(event);
        newEvent.setRelatedSkills(skills);
        newEvent.setOwner(owner);

        Event createdEvent = eventRepository.save(newEvent);
        return eventMapper.toDto(createdEvent);
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    public List<EventDto> getOwnedEvents(Long ownerId) {
        List<Event> events = eventRepository.findAllByUserId(ownerId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();

        return eventFilters.stream()
            .filter(filter -> filter.isApplicable(filters))
            .flatMap(filter -> filter.apply(events, filters))
            .map(eventMapper::toDto)
            .toList();
    }

    public EventDto get(Long id) {
        Event event = eventRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Couldn't find event with id: " + id));

        return eventMapper.toDto(event);
    }

    public EventDto updateEvent(EventDto event) {
        validateUserAccess(event.getRelatedSkills(), event.getOwnerId());

        EventDto existingEvent = get(event.getId());

        eventMapper.update(existingEvent, event);

        return create(existingEvent);
    }

    public void deleteAllByIds(List<Long> ids) {
        eventRepository.deleteAllById(ids);
    }

    public int removeUserFromEvents(List<Long> goalIds, Long userId) {
        List<Event> events = eventRepository.findAllById(goalIds);

        events.forEach(event -> {
            List<User> currentUsers = event.getAttendees();
            event.setAttendees(currentUsers.stream().filter(user -> !Objects.equals(user.getId(), userId)).toList());
        });

        return events.size();
    }

    public void clearPastEvents() {
        Stream<Event> allEvents = eventRepository.findAll().stream();
        EventFilterDto filters = EventFilterDto.builder().endDate(LocalDateTime.now()).build();

        List<Event> pastEvents = eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(allEvents, filters))
                .toList();

        int batchSize = Integer.parseInt("${scheduler.batch-size}");
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (int i = 0; i < pastEvents.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, pastEvents.size());
            List<Event> batch = pastEvents.subList(i, endIndex);

            executorService.execute(() -> eventRepository.deleteAllInBatch(batch));
        }
    }

}
