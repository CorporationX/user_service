package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
public class EventScheduler {

    private final EventService eventService;

    @Value("${scheduler.batchSize}")
    private int batchSize;

    // @Scheduled(cron = "${scheduler.deleteEventsCron}" если брать значение из yaml, то выдает ошибку
    @Scheduled(cron = "0 0 * * * *")
    public void clearEvents() {
        eventService.deletePastEvents(batchSize);
    }
}
