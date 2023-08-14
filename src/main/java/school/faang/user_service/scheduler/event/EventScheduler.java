package school.faang.user_service.scheduler.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import school.faang.user_service.service.event.EventService;

@RequiredArgsConstructor
@Slf4j
public class EventScheduler {

    private final EventService service;

    @Scheduled(cron = "${cron.daily}")
    public void clearEvents(){
        service.deletePastEvents();
        log.info("Started deleting past events");
    }
}