package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidation;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventValidation validation;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final ExecutorService completableFutureExecutor;

    @Value("${events.delete.count}")
    private int batchSize;

    public EventDto create(EventDto eventDto) {
        validation.validateEvent(eventDto);
        validation.validateUserSkills(eventDto);
        Event event = eventMapper.toEntity(eventDto);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
    }

    public EventDto getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .map(eventMapper::toDto)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Не найден ивент с айди %d", eventId)));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        return getEventsStreamByFilter(filter).map(eventMapper::toDto).toList();
    }

    public void deleteEvent(long eventId) {
        validation.validateEventId(eventId);
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        validation.validateEventId(eventDto.getId());
        validation.validateEvent(eventDto);
        validation.validateEventOwner(eventDto);
        validation.validateUserSkills(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional
    public void clearEvents() {
        EventFilterDto filterDto = EventFilterDto.builder()
                .eventStatusPattern(EventStatus.COMPLETED)
                .build();

        List<Long> eventsIds = getEventsStreamByFilter(filterDto)
                .map(Event::getId)
                .toList();

        if (eventsIds.isEmpty()) {
            return;
        }

        CompletableFuture.allOf(
                IntStream.range(0, (eventsIds.size() + batchSize - 1) / batchSize)
                        .mapToObj(i -> {
                            int start = i * batchSize;
                            int end = Math.min(start + batchSize, eventsIds.size());
                            List<Long> batch = eventsIds.subList(start, end);
                            return CompletableFuture.runAsync(() -> eventRepository.deleteByIds(batch),
                                    completableFutureExecutor);
                        }).toList().toArray(CompletableFuture[]::new))
                .join();
    }

    private Stream<Event> getEventsStreamByFilter(EventFilterDto filter) {
        Stream<Event> events = eventRepository.findAll().stream();

        Optional<EventFilter> firstApplicableFilter = eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .findFirst();

        if (firstApplicableFilter.isEmpty()) {
            return Stream.empty();
        }

        events = eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .reduce(events, (currentEvents, eventFilter) -> eventFilter.apply(currentEvents, filter), (a, b) -> a);

        return events;
    }
}
