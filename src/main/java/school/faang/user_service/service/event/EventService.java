package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class EventService {
    private final SkillRepository skillRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventDto create(EventDto eventDto) {
        checkOwnerHasRelatedSkills(eventDto);
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDto)));
    }

    @Transactional
    public EventDto getEvent(Long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId).orElseThrow(() ->
                new DataValidationException("Event not found")));
    }

    @Transactional
    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> events = eventRepository.findAll().stream();
        return filterEvents(events, filter)
                .map(eventMapper::toDto)
                .toList();
    }

    private Stream<Event> filterEvents(Stream<Event> events, EventFilterDto eventFilterDto) {
        return eventFilters.stream()
                .filter(filter -> filter.isApplicable(eventFilterDto))
                .reduce(events,
                        (streamEvents, filter) ->
                                filter.apply(streamEvents, eventFilterDto),
                        (Stream::concat));
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        checkOwnerHasRelatedSkills(eventDto);
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDto)));
    }

    @Transactional
    public List<EventDto> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    private void checkOwnerHasRelatedSkills(EventDto eventDto) {
          Optional.of(
                        skillRepository.findAllByUserId(eventDto.getOwnerId()).stream()
                                .map(Skill::getId)
                                .collect(toSet())
                )
                .filter(ids -> ids.containsAll(eventDto.getRelatedSkillsIds()))
                .orElseThrow(() -> new DataValidationException("Owner doesn't have all related skills"));
    }

    @Transactional
    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }
}
