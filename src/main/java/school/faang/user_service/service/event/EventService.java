package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;


    public EventDto create(EventDto eventDto) {
        validateRequiredSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(long eventId) {
        Optional<Event> foundEvent = eventRepository.findById(eventId);
        if (foundEvent.isPresent()) {
            Event gotEvent = foundEvent.get();
            return eventMapper.toDto(gotEvent);
        }
        throw new DataValidationException("This event is not exist");
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        List<Event> events = eventRepository.findAll();
        Stream<Event> eventsStream = events.stream();

        return eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(eventFilterDto))
                .reduce(eventsStream, (stream, eventFilter) -> eventFilter.apply(stream, eventFilterDto), (s1, s2) -> s1)
                .map(eventMapper::toDto).toList();
    }

    public long deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
        return eventId;
    }

    public EventDto updateEvent(EventDto event) {
        validateRequiredSkills(event);
        Event eventEntity = eventMapper.toEntity(event);
        Event savedEvent = eventRepository.save(eventEntity);
        return eventMapper.toDto(savedEvent);

    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);
        return eventMapper.toDtoList(events);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toDtoList(events);
    }


    private boolean hasRequiredSkills(EventDto event) {
        List<Long> requiredSkillIds = event.getRelatedSkills().stream().map(SkillDto::getId).collect(Collectors.toList());
        List<Long> ownerSkillIds = skillRepository.findAllByUserId(event.getOwnerId()).stream().map(Skill::getId).collect(Collectors.toList());
        return ownerSkillIds.containsAll(requiredSkillIds);
    }

    private void validateRequiredSkills(EventDto event) throws DataValidationException {
        if (!hasRequiredSkills(event))
            throw new DataValidationException("User does not have the required skills to conduct this event");

    }
}


