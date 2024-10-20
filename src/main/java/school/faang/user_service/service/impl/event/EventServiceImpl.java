package school.faang.user_service.service.impl.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.dto.event.EventDto;
import school.faang.user_service.model.dto.event.EventFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.event.Event;
import school.faang.user_service.model.event.EventStartEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventService;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final List<EventFilter> eventFilters;
    private final EventStartEventPublisher publisher;

    @Value("${scheduler.event-batch-size}")
    private int batchSize;

    @Override
    @Transactional
    public EventDto create(EventDto event) {
        eventValidator.validateOwnerSkills(event);
        Event eventEntity = eventMapper.toEntity(event);
        EventDto newEvent = eventMapper.toDto(eventRepository.save(eventEntity));
        log.info("Event created: {}", newEvent.id());
        return newEvent;
    }

    @Override
    public EventDto getEvent(Long eventId) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found for ID: " + eventId));
        return eventMapper.toDto(event);
    }

    @Override
    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();

        eventStream = eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filterDto))
                .reduce(eventStream,
                        (currentStream, eventFilter) -> eventFilter.apply(currentStream, filterDto),
                        (s1, s2) -> s2);

        return eventMapper.toListDto(eventStream.toList());
    }

    @Override
    public void deleteEvent(Long eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
            log.info("Event deleted: " + eventId);
        } else {
            log.warn("Event not found for ID: " + eventId);
        }
    }

    @Override
    public void deleteEvents(List<Event> events) {
        List<EventDto> eventDtos = eventMapper.toListDto(events);
        for (EventDto event : eventDtos) {
            eventValidator.validateEvent(event);
        }

        eventRepository.deleteAll(events);
    }

    @Override
    @Transactional
    public void deletePassedEvents() {
        var eventsToDelete = eventRepository.findAllByEndDateBefore(LocalDateTime.now());
        ListUtils.partition(eventsToDelete, batchSize)
                .forEach(this::deletePassedEventsByBatches);
    }

    @Override
    @Async("threadPool")
    public void deletePassedEventsByBatches(List<Event> events) {
        events.forEach((event) -> log.info("Deleting passed event with ID: {}", event.getId()));
        eventRepository.deleteAllInBatch(events);
    }

    @Override
    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateOwnerSkills(eventDto);

        Event eventEntity = eventMapper.toEntity(eventDto);
        Event saved = eventRepository.save(eventEntity);

        return eventMapper.toDto(saved);
    }

    @Override
    public List<Event> getOwnedEvents(Long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    @Override
    public List<Event> getParticipatedEvents(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public void findEventsStartingRightNow() {
        List<Event> events = eventRepository.findAllByStartDate(LocalDateTime.now());

        events.forEach(event -> {
            List<Long> participantsIds = event.getAttendees().stream()
                    .map(User::getId)
                    .toList();

            publisher.publish(new EventStartEvent(event.getId(), participantsIds));
            log.info("Event with id {} was sent to broker", event.getId());
        });
    }
}
