package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventStartEventService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class EventStartEventPublisher {

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final EventStartEventService eventStartEventService;

    public void publish(long eventId, LocalDateTime publishDate) {
        LocalDateTime currentTime = LocalDateTime.now();
        long delay = Duration.between(currentTime, publishDate).getSeconds();

        scheduledThreadPoolExecutor
                .schedule(() -> eventStartEventService.sendScheduledEvent(eventId), delay, TimeUnit.SECONDS);
    }
}