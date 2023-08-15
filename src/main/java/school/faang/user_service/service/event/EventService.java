package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.event.EventRepository;

@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;

    public boolean existsById(long id) {
        return eventRepository.existsById(id);
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }
}
