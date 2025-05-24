package school.faang.user_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import school.faang.user_service.kafka.AnalyticsCreatedEvent;

@Configuration
@KafkaListener(topics = "analytics-created-event-topic")
@Slf4j
public class AnalyticsEventHandler {

    @KafkaHandler
    public void handle(AnalyticsCreatedEvent event) {
        log.info("Received AnalyticsEvent: {}", event);
    }
}
