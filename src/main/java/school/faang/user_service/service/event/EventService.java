package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Setter
public class EventService {
    private final EventRepository eventRepository;

    @Value("${batchSize.eventDeletion}")
    private int batchSize;

    public void clearEvents() {
        List<Event> allEvents = eventRepository.findAll();
        List<Long> ids = allEvents.stream()
                .filter(event -> event.getStatus().equals(EventStatus.COMPLETED) || event.getStatus().equals(EventStatus.CANCELED))
                .map(Event::getId).toList();

        if (ids.isEmpty()) {
            return;
        }

        List<List<Long>> partitions = ListUtils.partition(ids, batchSize);

        for (List<Long> partition : partitions) {
            clearEventsAsync(partition);
        }
    }

    @Async("threadPoolExecutor")
    public void clearEventsAsync(List<Long> partition) {
        eventRepository.deleteAllById(partition);
    }
}
