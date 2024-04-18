package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.executor.ExecutorsConfig;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.validation.event.EventValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final EventValidator eventValidator;
    private final UserValidator userValidator;
    private final ExecutorsConfig executorsConfig;

    @Value("{$event.past.delete-batch}")
    private int batchSize;

    public void clearPastEvent() {
        List<Event> events = eventRepository.findAll();
        List<Event> postEvents = postEventFilter(events);

        if (!postEvents.isEmpty()) {
            List<List<Event>> batches = postEvents.stream()
                    .collect(Collectors.groupingBy(event -> Math.abs(event.hashCode() / 10)))
                    .values()
                    .stream()
                    .map(batch -> batch.subList(0, Math.min(batch.size(), batchSize)))
                    .toList();
            batches.parallelStream().forEach(batch -> executorsConfig.executorService().submit(() -> eventRepository.deleteAll(batch)));
        }
    }

    public EventDto create(EventDto eventDto) {
        eventValidator.validateUserHasRequiredSkills(eventDto);
        Event savedEvent = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(savedEvent);
    }

    public EventDto getEvent(long eventId) {
        Event searchedEvent = getEventFromRepository(eventId);
        return eventMapper.toDto(searchedEvent);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();

        if (!eventFilters.isEmpty()) {
            eventFilters.stream()
                    .filter(filter -> filter.isApplicable(filters))
                    .forEach(filter -> filter.apply(events, filters));
        }
        return eventMapper.toDto(events);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        userValidator.validateIfUserExistsById(userId);
        List<Event> userOwnedEvents = eventRepository.findAllByUserId(userId);
        return eventMapper.toDto(userOwnedEvents);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        userValidator.validateIfUserExistsById(userId);
        List<Event> userParticipatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toDto(userParticipatedEvents);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateUserHasRequiredSkills(eventDto);
        Event event = getEventFromRepository(eventDto.getId());
        eventValidator.validateUserIsOwnerOfEvent(event.getOwner(), eventDto);

        Event updatedAndSavedEvent = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(updatedAndSavedEvent);
    }

    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    private Event getEventFromRepository(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event doesn't exist by id: " + eventId));
    }

    private List<Event> postEventFilter(List<Event> events) {
        return events.stream()
                .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                .toList();
    }
}
