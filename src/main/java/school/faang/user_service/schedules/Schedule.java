package school.faang.user_service.schedules;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.EventService;

@Component
@AllArgsConstructor
public class Schedule {
    private EventService eventService;

    @Scheduled(cron = "${premium.schedule.removal-cron}")
    public void clearEvents(){eventService.clearPastEvents();
    }
}
