package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapImpl;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventMapImpl eventMap;
    private final List<EventFilter> eventFilters;
    private final SkillRepository skillRepository;

    @Transactional
    public EventDto create(EventDto eventDto) {
        skillCheck(eventDto);
        Event event = eventMap.toEntity(eventDto);
        System.out.println(event);
        eventRepository.save(event);
        return null;
    }

    @Transactional
    public EventDto getEvent(long eventId) {
         List<Event> events = eventRepository.findAllByUserId(eventId);
         return eventMapper.toDto((Event) events);
    }

    public EventDto getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        List<Event> eventList = events.toList();
        return eventMapper.toDto((Event) eventList);
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    public void updateEvent(EventDto eventDto) {
        skillCheck(eventDto);
        eventRepository.save(eventMapper.toEntity(eventDto));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> eventList = eventRepository.findAllByUserId(userId);
        return eventMapper.toDtoList(eventList);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    private void skillCheck(EventDto eventDto) {
        List<Long> userSkills = skillRepository.findSkillsOfferedToUser(eventDto.getOwnerId()).stream()
                .map(Skill::getId)
                .toList();
        List<Long> eventSkills = eventDto.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .toList();
        if (userSkills.size() != eventSkills.size()) {
            throw new DataValidationException("Недостаточно навыков для создания ивента");
        }
    }
}
