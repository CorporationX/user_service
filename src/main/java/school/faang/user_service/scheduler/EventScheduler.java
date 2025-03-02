package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventScheduler {
    private static final String DELETE_NAME_TASK = "delete passed events from database.";
    private final EventService eventService;

    @Scheduled(cron = "${cron.expression.delete_old_events}")
    public void clearEvents() {
        log.info("Task start: {}",DELETE_NAME_TASK);
        eventService.clearEvents();
        log.info("Task end: {}", DELETE_NAME_TASK);
    }
}
