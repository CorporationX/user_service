package school.faang.user_service.service.event;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.MapperEvent;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final MapperEvent mapperEvent;
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventDto create(EventDto eventDto) {
        Optional<User> userOptional = userRepository.findById(eventDto.getOwnerId());
        User user = userOptional.get();
            List<Skill> skillList = user.getSkills();
            for (Skill skill : skillList) {
                for (SkillDto skill2 : eventDto.getRelatedSkills()) {
                    if (skill.getTitle().equals(skill2.getTitle()))
                        eventRepository.save(mapperEvent.toEntity(eventDto));
                    return eventDto;
                }
            }
        return null;
    }

    public EventDto getEvent(Long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isPresent()){
                throw new DataValidationException("Нет эвентов");
            } else {
                return mapperEvent.toDto(eventOptional.get());
            }
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        List<Event> events = eventRepository.findAllEvent();
        if (filterDto == null) {
            return events.stream().map(mapperEvent::toDto).collect(Collectors.toList());
        } else {
            Stream<Event> stream = events.stream();
            List<Event> filterEvent = filterEvent(stream, filterDto).toList();
            return filterEvent.stream().map(mapperEvent::toDto).collect(Collectors.toList());
        }
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        if(eventRepository.findById(eventDto.getId()).isPresent()){
            Optional<User> userOptional = userRepository.findById(eventDto.getOwnerId());
            List<Skill> skillList = userOptional.get().getSkills();
            for (Skill skill : skillList) {
                for (SkillDto skill2 : eventDto.getRelatedSkills()) {
                    if (skill.getTitle().equals(skill2.getTitle()))
                        eventRepository.save(mapperEvent.toEntity(eventDto));
                    return eventDto;
                }
            }
        }
        return null;
    }

    public List<EventDto> getOwnedEvents(Long userId, EventFilterDto filterDto) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        if (filterDto == null) {
            return events.stream().map(mapperEvent::toDto).collect(Collectors.toList());
        } else {
            Stream<Event> stream = events.stream();
            List<Event> filterEvent = filterEvent(stream, filterDto).toList();
            return filterEvent.stream().map(mapperEvent::toDto).collect(Collectors.toList());
        }
    }

    public List<EventDto> getParticipatedEvents(Long eventId, EventFilterDto filterDto) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(eventId);
        if (filterDto == null) {
            return events.stream().map(mapperEvent::toDto).collect(Collectors.toList());
        } else {
            Stream<Event> stream = events.stream();
            List<Event> filterEvent = filterEvent(stream, filterDto).toList();
            return filterEvent.stream().map(mapperEvent::toDto).collect(Collectors.toList());
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
