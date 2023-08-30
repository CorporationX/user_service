package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.ScheduledEventService;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Setter
public class EventStartEventPublisher {

    private final ScheduledEventService scheduledEventService;
    @Value("${event.publisher.scheduler.delays.days}")
    private Set<Integer> daysDelay;
    @Value("${event.publisher.scheduler.delays.hours}")
    private Set<Integer> hoursDelay;
    @Value("${event.publisher.scheduler.delays.minutes}")
    private Set<Integer> minutesDelay;

    public void publish(long eventId, LocalDateTime publishDate) {
        scheduledEventService.sendScheduledEventStartEvent(daysDelay, hoursDelay, minutesDelay, eventId, publishDate);
    }
}