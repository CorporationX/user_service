package school.faang.user_service.scheduler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.event.EventStartEvent;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.service.event.EventService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class EventStartEventScheduler {

    private final EventService eventService;
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final EventStartEventPublisher eventStartEventPublisher;

    private List<Long> eventsToPublish;
    private List<Long> publishedEvents = new ArrayList<>();

    @Value("${scheduler.event-start-notification.upload-events-days-batch}")
    private Long uploadEventDaysBatch;

    @Value("${scheduler.event-start-notification.duration-to-publish-event-start-event-milliseconds}")
    private List<Long> durationsToPublishEventStartEvent;

    @Scheduled(cron = "${cron.expressions.load-upcoming-events}")
    public void loadUpcomingEvents() {
        List<Event> events = eventService.findEventsByStartDateBetween(LocalDateTime.now(), LocalDateTime.now().plusDays(uploadEventDaysBatch));
        eventsToPublish = events.stream()
                .map(Event::getId)
                .filter(id -> !publishedEvents.contains(id))
                .toList();
        log.info("Found upcoming events to publish: {}", eventsToPublish.size());
    }

    @Scheduled(cron = "${cron.expressions.clear-published-events}")
    public void clearPublishedEvents() {
        log.info("Total published events in memory: {}", publishedEvents.size());
        List<Event> events = eventService.findAllEventsByIds(publishedEvents);
        List<Long> eventsIds = events.stream().map(Event::getId).toList();
        publishedEvents = publishedEvents.stream().filter(eventsIds::contains).toList();
        log.info("Cleared published events. Current size: {}", publishedEvents.size());
    }

    @Scheduled(cron = "${cron.expressions.publish-event-start-event-fixed-rate}")
    public void schedulePreStartNotifications() {
        if (eventsToPublish == null || eventsToPublish.isEmpty()) {
            log.info("No event starts notifications to publish");
            return;
        }
        log.info("Scheduling {} event start notifications to publish", eventsToPublish.size());
        eventsToPublish.forEach(eventId -> durationsToPublishEventStartEvent
                .forEach(offsetBeforeEventStart -> scheduleNotificationIfNeeded(eventId, offsetBeforeEventStart)));
    }

    public void scheduleNotificationIfNeeded(long eventId, long offsetBeforeEventStart) {
        Event event = eventService.findEventWithAttendeesById(eventId);
        LocalDateTime eventStartTime = event.getStartDate();
        Instant eventStartTimeInstant = eventStartTime.atZone(ZoneId.systemDefault()).toInstant();
        Instant publishTime = eventStartTimeInstant.minusMillis(offsetBeforeEventStart);
        log.info("Calculated publishTime={} (UTC) for eventId={}", publishTime, eventId);
        if (eventStartTime.isBefore(LocalDateTime.now())) {
            creatingEventStartEventAndPublish(event);
        } else {
            log.info("Scheduling notification at {} (UTC) for eventId={}", publishTime, eventId);
            threadPoolTaskScheduler.schedule(() -> creatingEventStartEventAndPublish(event), publishTime);
        }
    }

    private void creatingEventStartEventAndPublish(Event event) {
        List<Long> attendeesIds = event.getAttendees().stream()
                .map(User::getId)
                .toList();
        EventStartEvent eventStartEvent = EventStartEvent.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventStartTime(event.getStartDate())
                .attendeesIds(attendeesIds)
                .build();
        eventStartEventPublisher.publish(eventStartEvent);
        publishedEvents.add(event.getId());
        log.info("Event start notification sent: eventId={}, channel='{}'",
                event.getId(),
                eventStartEvent.getClass().getSimpleName()
        );
    }
}