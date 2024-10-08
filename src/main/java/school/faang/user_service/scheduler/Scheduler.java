package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.impl.event.EventServiceImpl;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final EventServiceImpl eventServiceImpl;

    @Scheduled(cron = "${scheduler.clear-events-cron}")
    public void clearEvents() {
        eventServiceImpl.deletePassedEvents();
    }
}
