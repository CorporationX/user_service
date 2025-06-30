package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.subscription.filter.EventFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> filters;

    public EventDto create(EventDto eventDto) {
        User owner = getEventById(eventDto.getOwnerId()).getOwner();

        checkUserSkills(owner, eventDto.getRelatedSkills());
        Event event = eventMapper.toEntity(eventDto);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(Long eventId) {
        Event event = getEventById(eventId);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        List<Event> allEvents = eventRepository.findAll();

        Stream<Event> eventStream = allEvents.stream();

        for (EventFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                eventStream = filter.apply(eventStream, filterDto);
            }
        }

        return eventStream
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new DataValidationException("Event with id " + eventId + " not found.");
        }
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event event = getEventById(eventDto.getId());
        eventMapper.update(event, eventDto); // "Перенеси" все обновляемые поля из dto в event
        Event updated = eventRepository.save(event);
        return eventMapper.toDto(updated);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found with id: " + eventId));
    }

    public void checkUserSkills(User owner, List<Long> eventSkillIds) {
        if (!hasRequiredSkills(owner, eventSkillIds)) {
            throw new DataValidationException("User does not have the required skills to organize this event");
        }
    }

    private boolean hasRequiredSkills(User user, List<Long> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return true;
        }

        List<Long> userSkillIds = user.getSkills()
                .stream()
                .map(Skill::getId)
                .collect(Collectors.toList());

        return userSkillIds.containsAll(requiredSkills);
    }
}
