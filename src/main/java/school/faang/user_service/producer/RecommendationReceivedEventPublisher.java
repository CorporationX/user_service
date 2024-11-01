package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.RecommendationReceivedEvent;

@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic recommendationReceivedTopic;

    public void publish(RecommendationReceivedEvent event) {
        kafkaTemplate.send(recommendationReceivedTopic.name(), event);
    }
}
