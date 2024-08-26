package school.faang.user_service.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.event.EventDelayTime;
import school.faang.user_service.event.EventStartEvent;
import school.faang.user_service.event.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStartEventPublisherJob implements Job {

    @Value("${scheduler.event-start.interval-seconds:60}")
    private int intervalInSeconds;
    private final EventRepository eventRepository;
    private final EventStartEventPublisher eventStartEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public void execute(JobExecutionContext context) {
        Instant currentDateTimeUTC = Instant.now().truncatedTo(ChronoUnit.MINUTES);
        log.info("Starting event publisher job at UTC time {}", currentDateTimeUTC);

        for (EventDelayTime eventDelayTime : EventDelayTime.values()) {
            findAndPublishUpcomingEvents(eventDelayTime, currentDateTimeUTC);
        }
    }

    private void findAndPublishUpcomingEvents(EventDelayTime eventDelayTime, Instant currentDateTimeUTC) {
        Instant startDate = calculateStartDate(eventDelayTime, currentDateTimeUTC);
        Instant startDateOffset = startDate.plus(intervalInSeconds, ChronoUnit.SECONDS);

        log.info("Searching for events planned to start in '{}' with search time {} and event time {}",
                eventDelayTime, currentDateTimeUTC, startDate);

        List<Event> events = eventRepository.findByStartDateBetween(startDate, startDateOffset);
        log.info("Found {} events scheduled to start in '{}'", events.size(), eventDelayTime);

        events.forEach(event -> publishEventStart(event, eventDelayTime));
    }

    private Instant calculateStartDate(EventDelayTime eventDelayTime, Instant currentDateTimeUTC) {
        return currentDateTimeUTC.plusMillis(eventDelayTime.getTime());
    }

    private void publishEventStart(Event event, EventDelayTime eventDelayTime) {
        try {
            log.info("Publishing event: id={}, timeBeforeStart={}", event.getId(), eventDelayTime);
            eventStartEventPublisher.publish(new EventStartEvent(event.getId(),
                    event.getTitle(),
                    event.getStartDate(),
                    eventDelayTime));
        } catch (Exception e) {
            log.error("Failed to publish event: id={}", event.getId(), e);
        }
    }
}