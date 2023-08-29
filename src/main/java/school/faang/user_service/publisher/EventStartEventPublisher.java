package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.ScheduledEventService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStartEventPublisher {

    private final ScheduledEventService scheduledEventService;

    public void publish(long eventId, LocalDateTime publishDate) {
        List<LocalDateTime> publishingScheduledTime = calculateDelayTime(publishDate);

        for (int i = 0; i < 5; i++) {
            scheduledEventService.publishScheduledEvent(eventId,i,publishingScheduledTime.get(i));
        }
    }
    private List<LocalDateTime> calculateDelayTime(LocalDateTime publishDate){
        LocalDateTime first = publishDate.minusDays(1);
        LocalDateTime second = publishDate.minusHours(5);
        LocalDateTime third = publishDate.minusHours(1);
        LocalDateTime fourth = publishDate.minusMinutes(10);
        return List.of(first, second, third, fourth, publishDate);
    }
}
