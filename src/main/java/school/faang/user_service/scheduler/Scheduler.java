package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {
    private final EventService eventService;
    @Value("${scheduler.partition_size}")
    private int partitionSize;

    @Scheduled(cron = "${delete_past_events.cron}")
    public void clearEvents() {
        log.info("Clearing events");
        eventService.deletePastEvents(partitionSize);
    }
}
