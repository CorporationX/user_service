package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.event.EventRepository;

@RequiredArgsConstructor
@Service
public class EventService {
    private final EventRepository eventRepository;

    public boolean existsById(long id) {
        return eventRepository.existsById(id);
    }
}
