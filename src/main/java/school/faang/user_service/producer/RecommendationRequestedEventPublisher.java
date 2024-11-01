package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.RecommendationRequestedEvent;

@Component
@RequiredArgsConstructor
public class RecommendationRequestedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic recommendationRequestTopic;

    public void publish(RecommendationRequestedEvent recommendationRequestedEvent) {
        kafkaTemplate.send(recommendationRequestTopic.name(), recommendationRequestedEvent);
    }
}
