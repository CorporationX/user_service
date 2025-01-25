package school.faang.user_service.repository.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventRepositoryAdapter {

    private final EventRepository eventRepository;

    public Event getEventById(Long eventId) throws DataValidationException {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found with ID: " + eventId));
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
}