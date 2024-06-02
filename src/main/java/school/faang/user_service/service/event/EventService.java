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
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.validator.EventValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final UserRepository userRepository;
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        User user = userRepository.findById(eventDto.getOwnerId()).orElseThrow(() ->
                new DataValidationException("Пользователя с id: " + eventDto.getOwnerId() + " нет в базе данных."));
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(user);

        event.setRelatedSkills(List.of(Skill.builder().id(1L).build()));
        user.setSkills(List.of(Skill.builder().id(1L).build()));

        eventValidator.checkNeedSkillsForEvent(user, event);
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("События с id: " + eventId + " нет.")));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {

        return eventFilters.stream().filter(eventFilter -> eventFilter.isApplicable(filters))
                .flatMap(filter -> filter.apply(eventRepository.findAll().stream(), filters))
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        User user = userRepository.findById(eventDto.getOwnerId()).orElseThrow(() ->
                new DataValidationException("Пользователя с id: " + eventDto.getOwnerId() + " нет в базе данных."));
        Event event = eventMapper.toEntity(eventDto);

        eventValidator.checkUserIsOwnerEvent(user, event);
        eventValidator.checkNeedSkillsForEvent(user, event);
        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }
}