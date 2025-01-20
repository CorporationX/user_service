package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final List<EventFilter> eventFilters;

    public Event create(Event event) {
        if (event.getRelatedSkills().isEmpty()) {
            throw new DataValidationException("Event must have at least one related skill");
        }
        if (!ownerHasRequiredSkills(event)) {
            throw new DataValidationException("User does not have required skills to create the event");
        }
        event.setRelatedSkills(event.getRelatedSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());

        return eventRepository.save(event);
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No event found by id provided!"));
    }

    public List<Event> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));

        return events.toList();
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Event updateEvent(Event event) {
        if (!ownerHasRequiredSkills(event)) {
            throw new DataValidationException("User does not have required skills to create the event");
        }

        Event existingEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event does not exist"));
        existingEvent.setOwner(userRepository.findById(event.getOwner().getId())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist")));
        existingEvent.setRelatedSkills(event.getRelatedSkills().stream()
                .map(skill -> skillRepository.findById(skill.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
        return eventRepository.save(existingEvent);
    }

    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    private boolean userHasRequiredSkills(Long ownerId, List<Long> requiredSkills) {
        return userRepository.findById(ownerId).
                orElseThrow(() -> new IllegalArgumentException("User does not exist"))
                .getSkills().stream()
                .map(Skill::getId)
                .anyMatch(requiredSkills::contains);
    }

    private boolean ownerHasRequiredSkills(Event event) {
        return userRepository.findById(event.getOwner().getId()).
                orElseThrow(() -> new IllegalArgumentException("User does not exist"))
                .getSkills().stream()
                .map(Skill::getId)
                .anyMatch(event.getRelatedSkills().stream()
                        .map(Skill::getId).toList()
                        ::contains);
    }
}
