package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillService skillService;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final EventValidator eventValidator;

    @Transactional
    public EventDto create(EventDto eventDto) {
        User user = userService.findById(eventDto.getOwnerId());
        List<String> skillList = new ArrayList<>();
        for (Long skillId : eventDto.getRelatedSkills()) {
            Skill skill = skillService.getSkillById(skillId);
            skillList.add(skill.getTitle());
        }
        eventValidator.validate(skillList, user.getSkills());
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new DataValidationException("Такого эвента нет"));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        List<Event> events = eventRepository.findAll();
        return filterEventToList(events, filterDto);
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(Long eventId, EventUpdateDto eventUpdateDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new DataValidationException("Такого эвента нет"));
        List<String> skillList = new ArrayList<>();
        for (Long skillId : eventUpdateDto.getRelatedSkills()) {
            skillList.add(skillService.getSkillById(skillId).getTitle());
        }
        eventValidator.validate(skillList, event.getRelatedSkills());
        Event eventUpdate = eventMapper.toUpdateDto(eventUpdateDto);
        eventUpdate.setId(eventId);
        return eventMapper.toDto(eventRepository.save(eventUpdate));
    }

    public List<EventDto> getOwnedEvents(Long userId, EventFilterDto filterDto) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return filterEventToList(events, filterDto);
    }

    public List<EventDto> getParticipatedEvents(Long eventId, EventFilterDto filterDto) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(eventId);
        return filterEventToList(events, filterDto);
    }

    public List<EventDto> filterEventToList(List<Event> events, EventFilterDto filterDto) {
        if (filterDto == null) {
            return events.stream().map(eventMapper::toDto).collect(Collectors.toList());
        } else {
            Stream<Event> stream = events.stream();
            List<Event> filterEvent = filterEvent(stream, filterDto).toList();
            return filterEvent.stream().map(eventMapper::toDto).collect(Collectors.toList());
        }
    }

    public Stream<Event> filterEvent(Stream<Event> eventStream, EventFilterDto filterDto) {
        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(filterDto)) {
                eventStream = eventFilter.apply(eventStream, filterDto);
            }
        }
        return eventStream;
    }
}
