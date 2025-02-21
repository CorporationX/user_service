package school.faang.user_service.repository.adapter;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

@Component
@RequiredArgsConstructor
public class EventRepositoryAdapter {

    private final EventRepository eventRepository;

    public Event getEventById(Long eventId) {
        return eventRepository
                .findById(eventId)
                .orElseThrow(
                        () ->
                                new EntityNotFoundException(
                                        "Event with ID: " + eventId + " not found"));
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public void delete(Event event) {
        eventRepository.delete(event);
    }

    public List<Event> findAll(Specification<Event> specification) {
        return eventRepository.findAll(specification);
    }

    public List<Event> findAllByUserId(Long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    public void deleteAll(Iterable<Event> events) {
        eventRepository.deleteAll(events);
    }

    public List<Event> findParticipatedEventsByUserId(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }
}
