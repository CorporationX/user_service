package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventAsyncService {
    private final EventRepository eventRepository;

    @Async("taskExecutor")
    public void clearEventsPartition(List<Event> events) {
        eventRepository.deleteAll(events);
    }
}
