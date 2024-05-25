package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.publisher.EventStartEventPublisher;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class EventCheckerScheduler {

    private final EventRepository eventRepository;
    private final EventStartEventPublisher startEventPublisher;
    private final EventMapper eventMapper;

    @Value("${task.scheduling.interval-one-minute}")
    private long oneMinuteInterval;

    @Value("${task.scheduling.interval-five-hours}")
    private long fiveHoursInterval;


    @Transactional
    @Scheduled(fixedRateString = "${task.scheduling.fix-rate-oneminute}")
    public void checkUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        ZonedDateTime zonedNow = now.atZone(ZoneId.of("UTC"));

        checkAndPublishEventsStartingWithin(zonedNow, zonedNow.plusMinutes(oneMinuteInterval));
        checkAndPublishEventsStartingWithin(zonedNow.plusHours(fiveHoursInterval), zonedNow.plusHours(fiveHoursInterval).plusMinutes(1));
    }

    private void checkAndPublishEventsStartingWithin(ZonedDateTime start, ZonedDateTime end) {
        List<Event> upcomingEvents = eventRepository.findEventStartingBetween(start, end);
        upcomingEvents.forEach(event -> startEventPublisher.publish(eventMapper.toEventStartEvent(event)));
    }


}
