package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final UserRepository userRepository;

    public EventDto create(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        User user = userRepository.findById(eventDto.getOwnerId()).orElseThrow(() ->
                new DataValidationException("Пользователя с id: " + eventDto.getOwnerId() + " нет в базе данных."));

        checkNeedSkillsForEvent(user, event);
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("События с id: " + eventId + " нет.")));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();

        eventFilters.stream().filter(eventFilter -> eventFilter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        return eventMapper.toEventsDto(events.toList());
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        User user = userRepository.findById(eventDto.getOwnerId()).orElseThrow(() ->
                new DataValidationException("Пользователя с id: " + eventDto.getOwnerId() + " нет в базе данных."));

        if (!event.getOwner().equals(user)) {
            throw new DataValidationException("Пользователь не является автором события " + event.getTitle());
        }
        checkNeedSkillsForEvent(user, event);
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    private void checkNeedSkillsForEvent(User user, Event event) {
        if (!new HashSet<>(user.getSkills()).containsAll(event.getRelatedSkills())) {
            throw new DataValidationException("Пользователь " + user.getUsername()
                    + " не может провести такое событие с такими навыками.");
        }
    }
}