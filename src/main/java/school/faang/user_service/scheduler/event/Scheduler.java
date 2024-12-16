package school.faang.user_service.scheduler.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
    private final EventService eventService;

    @Scheduled(cron = "${application.scheduler.cron.one_day}")
    public void clearEvents() {
        log.info("Starting to clear events that are expired from now!!!");
        eventService.clearExpiredEvents();
    }
}
