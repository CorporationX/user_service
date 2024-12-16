package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final EventDtoValidator eventValidator;
    private final List<EventFilter> eventFilters;
    private final int THREAD_SIZE = 4;

    @Value("${application.scheduler.batch-size}")
    private int BATCH_SIZE;

    public EventDto create(EventDto eventDto) {
        eventValidator.validate(eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userService.findById(eventDto.getOwnerId()));
        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    public EventDto getEvent(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There is no event with this id"));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilters(EventFilterDto filters) {
        Stream<Event> events = eventRepository.findAll().stream();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));

        return eventMapper.toListDto(events.toList());
    }

    public void deleteEvent(long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
        } else {
            throw new DataValidationException("There is no event with this id");
        }
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validate(eventDto);
        Event event = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new DataValidationException("There is no event with this id"));
        Event updatedEvent = eventMapper.toEntity(eventDto);
        updatedEvent.setOwner(event.getOwner());
        eventRepository.save(updatedEvent);
        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findParticipatedEventsByUserId(userId));
    }

    @Async("removeExpiredEvent")
    public CompletableFuture<Void> clearExpiredEvents() {
        List<Event> allEvents = eventRepository.findAll().stream()
                .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                .toList();
        if (!allEvents.isEmpty()) {
            List<Long> eventIds = allEvents.stream().map(Event::getId).toList();
            for (int i = 0; i < allEvents.size(); i += BATCH_SIZE) {
                List<Long> batchIds = eventIds.subList(i, Math.min(i + BATCH_SIZE, allEvents.size()));
                eventRepository.deleteAllById(batchIds);
            }
        }
        return CompletableFuture.completedFuture(null);
    }
}
