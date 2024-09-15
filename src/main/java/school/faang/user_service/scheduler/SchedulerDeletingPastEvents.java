package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import school.faang.user_service.service.event.EventService;

@Configuration
@RequiredArgsConstructor
public class SchedulerDeletingPastEvents {
    private final EventService eventService;

    @Scheduled(cron = "${scheduler.cron.expression}")
    public void clearEvents() {
        eventService.deletingAllPastEvents();
    }
}