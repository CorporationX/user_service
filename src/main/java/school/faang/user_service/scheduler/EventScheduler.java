package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.EventService;

@Component
@RequiredArgsConstructor
public class EventScheduler {

    private final EventService eventService;

    @Value("${scheduler.batchSize}")
    private int batchSize;

    @Scheduled(cron = "${scheduler.deleteEventsCron}")
    public void clearEvents() {
        eventService.deletePastEvents(batchSize);
    }
}
