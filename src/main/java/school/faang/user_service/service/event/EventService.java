package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.messaging.StartEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final StartEventPublisher startEventPublisher;

    public EventDto create(EventDto event) {
        User user = userRepository.findById(event.getId()).orElseThrow(
                () -> new DataValidationException("Event " + event.getId() + " with this id was not found in the method create"));
        if (!(isUserContainsSkill(event, user))) {
            throw new DataValidationException("The event " + event.getId() +
                    " cannot be held with such skills at the user " + user.getId() + " in the method create");
        }
        EventDto eventDto = eventMapper.toEventDto(eventRepository.save(eventMapper.toEvent(event)));
        startEventPublisher.publish(event);
        return eventDto;
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toEventDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event " + eventId + "with this id was not found in the method getEvent")));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();

        List<EventFilter> eventFilterList = eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        for (EventFilter filter : eventFilterList) {
            events = filter.apply(events, filters);
        }
        return events.map(eventMapper::toEventDto).toList();
    }

    public EventDto updateEvent(EventDto event) {
        Event event1 = eventRepository.findById(event.getId())
                .orElseThrow(() -> new DataValidationException(
                        "The event " + event.getId() + " did not pass validation when updating the event in the method updateEvent"));
        eventMapper.update(event1, event);
        return eventMapper.toEventDto(eventRepository.save(event1));
    }

    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    public void deleteEvent(long eventId) {
        if (eventId <= 0) {
            throw new DataValidationException("Event " + eventId + " does not exist");
        }
        eventRepository.deleteById(eventId);
    }

    private boolean isUserContainsSkill(EventDto event, User user) {
        return new HashSet<>(user.getSkills()
                .stream()
                .map(Skill::getTitle)
                .toList())
                .containsAll(event.getRelatedSkills()
                        .stream()
                        .map(SkillDto::getTitle).toList());
    }
}
