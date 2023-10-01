package school.faang.user_service.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessagePublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String channel, Object message) {
        kafkaTemplate.send(channel, message)
                .thenAccept(ack -> log.info("{} notification was published to kafka. {}", channel, message))
                .exceptionallyAsync(e -> {
                    log.error("Failed to publish notification, {}", e.getMessage(), e);
                    return null;
                });
    }
}
