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
public class SchedulerImpl implements Scheduler {

    private final EventService eventService;

    @Value("${scheduling.cron}")
    private String cronExpression;

    @Override
    @Scheduled(cron = "${scheduling.cron}")
    public void clearEvents() {
        log.info("Schedule used {}", cronExpression);
        eventService.deletingExpiredEvents();
    }
}
