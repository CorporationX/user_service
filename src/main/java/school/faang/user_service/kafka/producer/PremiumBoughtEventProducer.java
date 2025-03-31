package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.kafka.events.PremiumBoughtEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumBoughtEventProducer {

    private final KafkaTemplate<String, PremiumBoughtEvent> kafkaTemplate;
    @Value("${spring.kafka.topics.premium-bought}")
    private String topic;

    public void sendEvent(PremiumBoughtEvent event) {
        kafkaTemplate.send(topic, event);
        log.info("Sent event to Kafka: {}", event);
    }
}
