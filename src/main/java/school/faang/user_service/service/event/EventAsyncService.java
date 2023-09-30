package school.faang.user_service.service.event;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
public class EventAsyncService {
    private final EventRepository eventRepository;
    private final ThreadPoolTaskExecutor taskExecutor;

    public EventAsyncService(EventRepository eventRepository,
                             @Qualifier("removeEventsExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.eventRepository = eventRepository;
        this.taskExecutor = taskExecutor;
    }

    @Async("removeEventsExecutor")
    public void clearEventsPartition(List<Event> events) {
        eventRepository.deleteAll(events);
    }
}
