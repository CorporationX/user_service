package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.PremiumBoughtEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic premiumBoughtTopic;

    public void publish(PremiumBoughtEvent event) {
        kafkaTemplate.send(premiumBoughtTopic.name(), event);
    }
}