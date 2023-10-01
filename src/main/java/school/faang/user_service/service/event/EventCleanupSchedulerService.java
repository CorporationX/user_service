package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class EventCleanupSchedulerService {
    private final EventService eventService;
    private final ThreadPoolExecutor scheduledEventCleanupThreadPoolExecutor;

    @Value("${event.cleanup.batch.size}")
    private int batchSize;

    @Scheduled(cron = "${event.cleanup.scheduler.cron}")
    public void clearPastEvents() {
        log.info("Event cleanup start");
        LocalDateTime currentTime = LocalDateTime.now();
        Set<Long> pastEventIds = ConcurrentHashMap.newKeySet();
        List<Event> events = eventService.getAllEvents();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < events.size(); i += batchSize) {
            List<Event> batch = events.subList(i, Math.min(i + batchSize, events.size()));
            futures.add(CompletableFuture.runAsync(() -> batch.stream()
                    .filter(event -> event.getEndDate().isBefore(currentTime))
                    .forEach(event -> pastEventIds.add(event.getId())), scheduledEventCleanupThreadPoolExecutor));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        eventService.deleteByIds(pastEventIds);
        log.info("Event cleanup has been completed");
    }
}
