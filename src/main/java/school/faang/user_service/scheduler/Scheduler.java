package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.EventService;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final EventService eventService;

    @Scheduled(cron = "${events.clean_interval}")
    public void clearEvents() {
        eventService.deleteEndEvents();
    }
}
