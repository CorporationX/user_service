package school.faang.user_service.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDTO;
import school.faang.user_service.dto.event.EventFilterDTO;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final EventUtil eventUtil = new EventUtil();

    @Value("${thread-pool.delete-events-max-threads}")
    private int deleteEventsThreads;

    @Autowired
    public EventService(EventRepository eventRepository1, UserRepository userRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository1;
        this.userRepository = userRepository;
        this.eventMapper = eventMapper;
    }

    public EventDTO create(EventDTO event) {
        User eventOwner = userRepository.findById(event.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Owner not found"));
        if (eventUtil.isValid(event) && eventUtil.checkOwnerSkills(eventOwner, event)) {
            Event entityEvent = eventMapper.eventDTOToEvent(event);
            entityEvent.setOwner(userRepository.findById(event.getOwnerId())
                    .orElseThrow(() -> new DataValidationException("Owner not found")));
            eventRepository.save(entityEvent);
            return eventMapper.eventToEventDTO(entityEvent);
        } else {
            log.error(event.toString());
            throw new RuntimeException("Event was not created");
        }
    }

    public EventDTO getById(Long id) {
        return eventMapper.eventToEventDTO(eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Event not found")));
    }

    public EventDTO update(Long eventId, EventDTO event) {
        Event updatedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found"));
        if (!eventId.equals(event.getId())) {
            throw new DataValidationException("Mismatched event ID");
        }
        User eventOwner = userRepository.findById(event.getOwnerId())
                .orElseThrow(() -> new DataValidationException("Owner not found"));
        if (!eventUtil.checkOwnerSkills(eventOwner, event) || !eventUtil.isValid(event)) {
            log.error("Event was not accept a validator or not check skills : \n {}", event);
        }
        eventMapper.updateEventFromDTO(event, updatedEvent);
        updatedEvent.setOwner(eventOwner);
        eventRepository.save(updatedEvent);
        return eventMapper.eventToEventDTO(updatedEvent);
    }

    public void delete(Long id) {
        var wasDeletedEvent = eventRepository.findById(id).orElseThrow(()
                -> new DataValidationException("Event not found"));
        log.info("Event deleted ID: {}", wasDeletedEvent.getId());
        eventRepository.delete(wasDeletedEvent);
    }

    public List<EventDTO> getOwnedEvents(Long ownerId) {
        List<Event> userEvents = eventRepository.findAllByUserId(ownerId);
        if (userEvents.isEmpty()) {
            throw new DataValidationException("Owner haven`t events");
        }
        return eventMapper.eventsToEventDTOs(userEvents);
    }

    public List<EventDTO> getParticipatedEvents(Long userId) {
        List<Event> userEvents = eventRepository.findParticipatedEventsByUserId(userId);
        if (userEvents.isEmpty()) {
            throw new DataValidationException("Haven`t participated events");
        }
        return eventMapper.eventsToEventDTOs(userEvents);
    }

    public List<EventDTO> getEventsByFilter(EventFilterDTO filter) {
        if (filter == null) {
            throw new DataValidationException("Filter is empty");
        }
        List<Event> eventsList = eventRepository.findAll();
        if (eventsList.isEmpty()){
            throw new DataValidationException("Nothing to show");
        }
        Stream<EventDTO> filteredEventDTOs = eventMapper.eventsToEventDTOs(eventsList).stream()
                .filter(eventDTO ->
                        eventDTO.getLocation()
                                .equals(filter.getLocation()));
        return filteredEventDTOs.toList();
    }

    public void deletePastEvents() {
        List<Event> events = eventRepository.findPastEventsByDateEnd(LocalDateTime.now());

        if (events.isEmpty()) {
            return;
        }

        int portion = events.size() / deleteEventsThreads;
        if (portion > 0) {
            deleteEventsByPortions(events, portion);
        } else {
            eventRepository.deleteAll(events);
        }
    }

    private void deleteEventsByPortions(List<Event> events, int portion) {
        ExecutorService executorService = Executors.newFixedThreadPool(deleteEventsThreads);

        for (int i=0; i<deleteEventsThreads; i++) {
            int startIndex = i * portion;
            int endIndex = (i == deleteEventsThreads-1) ? events.size() : startIndex+portion;

            var list = new ArrayList<>(events.subList(startIndex, endIndex));
            executorService.execute(() -> eventRepository.deleteAll(list));
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Thread was interrupted while deleting events", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
