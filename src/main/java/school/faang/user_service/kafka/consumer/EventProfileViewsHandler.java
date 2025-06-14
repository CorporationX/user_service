package school.faang.user_service.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import school.faang.user_service.kafka.events.AnalyticsEvent;
import school.faang.user_service.redis.promotion.PromotionAnalyticsCacheService;

import java.util.List;

@Configuration
@KafkaListener(
        topics = "analytics-profile-event-topic",
        containerFactory = "kafkaListenerContainerFactory"
)
@Slf4j
@RequiredArgsConstructor
public class EventProfileViewsHandler {
    private final PromotionAnalyticsCacheService redisService;

    @KafkaHandler
    public void handle(List<AnalyticsEvent> events) {
        log.info("Received AnalyticsEvent: {}", events);
        redisService.incrementEventsCounter(events);
    }
}
