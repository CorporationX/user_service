package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventClearScheduler {

    private final EventRepository eventRepository;

    private final EventService eventService;

    @Setter
    @Value("${scheduler.thread-count}")
    private int threadCounts;

    @Setter
    @Value("${scheduler.cron}")
    private String cron;

    @Scheduled(cron = "${scheduler.cron}")
    public void clearEvents() {

        System.out.println(threadCounts);
        System.out.println(cron);

        List<Event> events = eventRepository.findAll();
        List<Event> outdatedEvents = events.stream()
                .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                .toList();

        long eventsPerList = outdatedEvents.size() / threadCounts;
        long remainder = outdatedEvents.size() % threadCounts;

        if (eventsPerList != 0 ) {
            List<Event> toClear = new ArrayList<>();
            for (int i = 0; i < outdatedEvents.size() - remainder; i++) {
                toClear.add(outdatedEvents.get(i));
                if (i + 1 % eventsPerList == 0) {
                    eventService.clearOutdatedEvents(toClear);
                    toClear = new ArrayList<>();
                }
            }
        }

        if (remainder != 0) {
            outdatedEvents = outdatedEvents.subList((int) (outdatedEvents.size() - remainder), outdatedEvents.size());
            eventService.clearOutdatedEvents(outdatedEvents);
        }
    }
}
