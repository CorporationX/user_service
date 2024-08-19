package school.faang.user_service.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import school.faang.user_service.service.event.EventService;

public class Scheduler {

    private final EventService eventService;

    @Autowired
    public Scheduler(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(cron = "${scheduler.clear-events.cron}")
    public void clearEvents() {
        eventService.clearPastEvents();
    }
}
