package school.faang.user_service.scheduler.events;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
@RequiredArgsConstructor
public class ClearEvents {
    private final EventService eventService;

    @Scheduled(cron = "${scheduler.clearEvents.cronExpression}")
    public void clearEvents() {
        eventService.clearEvents();
    }
}