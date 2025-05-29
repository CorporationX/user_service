package school.faang.user_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import school.faang.user_service.kafka.AnalyticsEvent;
import school.faang.user_service.redis.AnalyticsProperties;
import school.faang.user_service.redis.RedisAnalyticsService;

import java.util.List;

@Configuration
@KafkaListener(
        topics = "analytics-profile-event-topic",
        containerFactory = "kafkaListenerContainerFactory"
)
@Slf4j
@RequiredArgsConstructor
public class AnalyticsEventHandler {
    private final RedisAnalyticsService redisService;
    private final AnalyticsProperties analyticsProperties;


    @KafkaHandler
    public void handle(List<AnalyticsEvent> events) {
        log.info("Received AnalyticsEvent: {}", events);


        long newCount = redisService.processEvent(events);

        if (newCount == analyticsProperties.getCounterThreshold()) {
            log.info("Type={} Id={} reached level {}",
                    events.getEventType(),
                    events.getReceiverId(),
                    newCount);

/*            switch (event.getEventType()) {
                case PROFILE_VIEW:
                    repo.updateProfileViews(event.getReceiverId(), newCount);
                    break;
                case EVENT_VIEW:
                    repo.updateEventViews(event.getReceiverId(), newCount);
                    break;
                default:
                    // не должно случиться, т.к. фильтруем раньше
            }*/


        }
    }
}
