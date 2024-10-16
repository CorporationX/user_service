package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class Scheduler {

    @Value("${events.batch.size}")
    private int batchSize;

    private final EventService eventService;

    @Scheduled(cron = "${events.cron}")
    public void clearEvents() {
        List<Event> events = eventService.getPastEvents();
        try {
            if (!events.isEmpty()) {
                ListUtils.partition(events, batchSize).forEach(
                        list -> eventService.deleteEventsByIds(list.stream().map(Event::getId).toList())
                );
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
