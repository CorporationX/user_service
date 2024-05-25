package school.faang.user_service.service.event;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventStartEvent;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.publisher.EventStartEventPublisher;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final EventStartEventPublisher startEventPublisher;

    private final ExecutorService executorService;
    @Value("${clearEvents.batchSize}")
    private int batchSize;


    public EventDto createEvent(EventDto eventDto) {
        eventValidator.validateEventInService(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    @Transactional
    public void startEvent(long id) {

        Event event = eventRepository.findById( id )
                .orElseThrow( () -> new EntityNotFoundException( " event not found with id " + id ) );

        EventStartEvent startEvent = eventMapper.toEventStartEvent( event );

        startEventPublisher.publish( startEvent );

    }

    public EventDto getEvent(long eventId) {
        return eventMapper.toDto(eventRepository.findById(eventId).orElseThrow(() -> new DataValidationException("Invalid event id")));
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();
        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                eventStream = eventFilter.apply(eventStream, eventFilterDto);
            }
        }
        return eventMapper.toDto(eventStream.toList());
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateEventInService(eventDto);
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDto)));
    }

    public List<EventDto> getOwnedEvents(Long ownerId){
        List<Event> events = eventRepository.findAllByUserId(ownerId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    public List<EventDto> getParticipatedEvents(Long userId){
        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        return events.stream().map(eventMapper::toDto).toList();
    }

    @Transactional
    public void clearEvents() {
        List<Event> allEvents = eventRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        List<Event> expiredEvents = allEvents.stream().filter(event -> event.getEndDate().isBefore(now)).toList();
        List<List<Event>> batches = Lists.partition(expiredEvents, batchSize);
        for (List<Event> batch : batches) {
            executorService.execute(() ->eventRepository.deleteAll(batch));
        }
    }
}