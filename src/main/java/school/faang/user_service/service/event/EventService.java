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

    public EventDto create(EventDto event) {
        User user = userRepository.findById(event.getId()).orElseThrow();
        if (!(isUserContainsSkill(event, user))) {
            throw new DataValidationException("The user cannot hold such an event with such skills");
        }
        return eventMapper.toEventDto(eventRepository.save(eventMapper.toEvent(event)));
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toEventDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("User with this id was not found")));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> event = eventRepository.findAll().stream();

        List<EventFilter> eventFilterList = eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        for (EventFilter events : eventFilterList) {
            event = events.apply(event, filters);
        }
        return event.map(eventMapper::toEventDto).toList();
    }

    public EventDto updateEvent(EventDto event) {
        Event event1 = eventRepository.findById(event.getId())
                .orElseThrow(() -> new DataValidationException(
                        "The event did not pass validation when updating the event"));
        eventMapper.update(event1, event);
        return create(eventMapper.toEventDto(event1));
    }

    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public void getParticipatedEvents(long userId) {
        eventRepository.findParticipatedEventsByUserId(userId);
    }

    public void deleteEvent(long eventId) {
        if (eventId <= 0) {
            throw new DataValidationException("Event does not exist");
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
