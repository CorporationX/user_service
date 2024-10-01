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

    @Scheduled(cron = "${schedule.clean_past_events.clean_interval}")
    public void cleanEvents() {
        eventService.deletePastEvents();
    }
}
