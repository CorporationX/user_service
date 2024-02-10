package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Setter
public class EventService {
    private final EventRepository eventRepository;
    private final ThreadPoolExecutor threadPoolExecutor;
    @Value("${batchSize.eventDeletion}")
    private int batchSize;

    public void clearEvents() {
        List<Event> allEvents = eventRepository.findAll();
        List<Long> ids = allEvents.stream()
                .filter(event -> event.getStatus().equals(EventStatus.COMPLETED) || event.getStatus().equals(EventStatus.CANCELED))
                .map(Event::getId).toList();

        if (ids.isEmpty()) {
            throw new IllegalArgumentException("There are no completed events in DB");
        }

        List<List<Long>> partitions = new ArrayList<>();

        for (int i = 0; i < ids.size(); i += batchSize) {
            partitions.add(ids.subList(i, Math.min(i + batchSize, ids.size())));
        }

        for (List<Long> partition : partitions) {
            threadPoolExecutor.execute(() -> {
                try {
                    eventRepository.deleteAllById(partition);
                } catch (Exception e) {
                    log.error("An error occurred during event deletion", e);
                }
            });
        }
    }
}
