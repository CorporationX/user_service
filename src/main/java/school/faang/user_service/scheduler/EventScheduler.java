package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class EventScheduler {
    private final EventService eventService;

    @Scheduled(cron = "${app.scheduler.event.start_event.cron}")
    public void startEvent() {
        LocalDateTime from = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime to = from.plusMinutes(1).minusSeconds(1);
        eventService.startEventsFromPeriod(from, to);
    }
}
