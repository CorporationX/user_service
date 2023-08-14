package school.faang.user_service.scheduler.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import school.faang.user_service.service.event.EventService;

@RequiredArgsConstructor
public class EventScheduler {

    private final EventService service;

    @Scheduled(cron = "${cron.daily}")
    public void clearEvents(){
        service.deletePastEvents();
    }
}