package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.thread.ThreadPoolDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ThreadPoolDistributor threadPoolDistributor;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters; //= List.of(new EventByOwnerFilter(), new EventTitleFilter());
    private final EventValidator eventValidator;

    public EventDto create(EventDto eventDto) {
        eventValidator.inputDataValidation(eventDto);
        Event eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    public EventDto getEventById(long eventId) {
        eventValidator.eventValidation(eventId);
        Optional<Event> event = eventRepository.findById(eventId);
        return eventMapper.toDto(event.orElse(null));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();

        return eventFilters.stream().filter(filter ->filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(events, filters))
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(Long eventId) {
        eventValidator.eventValidation(eventId);
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(Long eventId, EventDto eventDto) {
        eventValidator.eventValidation(eventId);
        eventValidator.inputDataValidation(eventDto);
        Event eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Событие id" + eventId + " не найдено"));
        eventEntity = eventMapper.toEntity(eventDto);
        return eventMapper.toDto(eventRepository.save(eventEntity));
    }

    @Transactional
    public void deletingAllPastEvents() {
        List<Event> allEvents = eventRepository.findByStatus(EventStatus.COMPLETED);
        int quantityThreadPollSize = threadPoolDistributor.customThreadPool().getPoolSize();
        int sizeFullListEvent = allEvents.size();
        int baseSize = sizeFullListEvent / quantityThreadPollSize;
        int remainder = sizeFullListEvent % quantityThreadPollSize;
        int startIndex = 0;
        for (int i = 0; i < quantityThreadPollSize; i++) {
            int currentSize = baseSize + (i < remainder ? 1 : 0);
            int endIndex = startIndex + currentSize;
            List<Event> partListEvent = new ArrayList<>(allEvents.subList(startIndex, Math.min(endIndex, sizeFullListEvent)));
            threadPoolDistributor.customThreadPool().submit(() -> {
                eventRepository.deleteAllInBatch(partListEvent);
            });
            startIndex = endIndex;
        }
    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toDtoList(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toDtoList(eventRepository.findParticipatedEventsByUserId(userId));
    }
}