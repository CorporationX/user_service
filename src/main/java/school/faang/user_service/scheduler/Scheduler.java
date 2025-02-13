package school.faang.user_service.scheduler;

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

    @Scheduled(cron = "${app.config.clear_events_sheduled_cron}") //every week
    public void clearEvents() {
        eventService.removeAllPastEvents();
        log.info("Removing all past events");
    }
}
