package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
public class EventScheduler {
    @Value("${spring.scheduler.partitionSize}")
    private int partitionSize;
    private final EventService eventService;

    @Scheduled(cron = "${spring.scheduler.cron}")
    public void clearEvents() {
        eventService.clearEvents(partitionSize);
    }
}
