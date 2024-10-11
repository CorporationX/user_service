package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
@Slf4j
public class OverdueEventsScheduler {

    private final EventService eventService;

    @Scheduled(cron = "${event.cron}")
    public void clearEvents() {
        log.info("Schedule used {}", eventService);
        eventService.deleteExpiredEvents();
    }
}
