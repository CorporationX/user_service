package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.exception.event.EventNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final List<EventFilter> eventFilters;

    @Transactional
    @Retryable(value = {DataValidationException.class})
    public EventDto create(EventDto eventDto) {
        eventValidator.validSkillUserInSkillEvent(eventDto);
        Event event = eventRepository.save(eventMapper.eventDtoToEvent(eventDto));

        return eventMapper.eventToEventDto(event);
    }

    public EventDto getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .map(eventMapper::eventToEventDto)
                .orElseThrow(() -> {
                    log.error(ExceptionMessages.EVENT_NOT_FOUND, eventId);
                    throw new DataValidationException(String.format(ExceptionMessages.EVENT_NOT_FOUND, eventId));
                });
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> events = eventRepository.findAll().stream();
        List<Event> filterEvents = eventFilters.stream()
                .filter(filterOne -> filterOne.isApplicable(filter))
                .reduce(events, (cumulativeStream, filterOne) ->
                        filterOne.apply(cumulativeStream, filter), Stream::concat)
                .toList();

        return filterEvents.stream()
                .map(eventMapper::eventToEventDto)
                .toList();
    }

    @Transactional
    @Retryable(value = {EventNotFoundException.class})
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validSkillUserInSkillEvent(eventDto);
        Event event = eventRepository.save(eventMapper.eventDtoToEvent(eventDto));

        return eventMapper.eventToEventDto(event);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> events = eventRepository.findAllByUserId(userId);

        return events.stream().map(eventMapper::eventToEventDto).toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);

        return events.stream().map(eventMapper::eventToEventDto).toList();
    }
}