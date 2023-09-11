package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class Scheduler {
    @Autowired
    private final EventService eventService;
    @Value("${scheduler.clearEventsBatchSize}")
    private int batchSize;

    @Scheduled(cron = "${scheduler.clearEventsCron}")
    public void clearEvents() {
        List<Event> eventsToDelete = eventService.getEventsToDelete();

        List<List<Event>> eventSubLists = ListUtils.partition(eventsToDelete, batchSize);

        eventSubLists.parallelStream().forEach(subList -> {
            if (!CollectionUtils.isEmpty(subList)) {
                eventService.deleteEvents(subList);
            }
        });
    }
}
