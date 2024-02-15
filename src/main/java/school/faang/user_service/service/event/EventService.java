package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.exception.event.EventValidator;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService extends EntityNotFoundException {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final EventValidator eventValidator;

    @Transactional
    public EventDto create(EventDto eventDto) {
        User user = userService.getUser(eventDto.getOwnerId());
        List<String> skillList = new ArrayList<>();
        for (Long skillId : eventDto.getRelatedSkills()) {
            skillRepository.findById(skillId).ifPresent(skill -> skillList.add(skill.getTitle()));
        }
        eventValidator.validate(skillList, user.getSkills());
        eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDto)));
    }

    public EventDto getEvent(Long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        eventValidator.validate(eventOptional);
        return eventOptional.map(eventMapper::toDto).orElse(null);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        List<Event> events = eventRepository.findAll();
        return filterHelp(events, filterDto);
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(Long eventId, EventUpdateDto eventUpdateDto) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        eventValidator.validate(eventOptional);
        User user = userService.getUser(eventUpdateDto.getOwnerId());
        List<String> skillList = new ArrayList<>();
        for (Long skillId : eventUpdateDto.getRelatedSkills()) {
            skillList.add(skillRepository.findById(skillId).get().getTitle());
        }
        eventValidator.validate(skillList, eventOptional.get().getRelatedSkills());
        Event event = eventMapper.toUpdateDto(eventUpdateDto);
        event.setId(eventId);
        return eventMapper.toDto(eventRepository.save(event));
    }

    public List<EventDto> getOwnedEvents(Long userId, EventFilterDto filterDto) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return filterHelp(events, filterDto);
    }

    public List<EventDto> getParticipatedEvents(Long eventId, EventFilterDto filterDto) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(eventId);
        return filterHelp(events, filterDto);
    }

    public List<EventDto> filterHelp(List<Event> events, EventFilterDto filterDto) {
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
