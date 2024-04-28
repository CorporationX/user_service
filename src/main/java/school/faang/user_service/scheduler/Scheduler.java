package school.faang.user_service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import school.faang.user_service.service.event.EventService;

public class Scheduler {

    private EventService eventService;

    @Scheduled(cron = "$scheduler.cron-expression-weekly")
    public void clearPastEvents(){
        eventService.clearPastEvent();
    }
}
