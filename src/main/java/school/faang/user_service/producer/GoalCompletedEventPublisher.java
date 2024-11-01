package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.GoalCompletedEvent;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic goalCompletedTopic;

    public void publish(GoalCompletedEvent event) {
        kafkaTemplate.send(goalCompletedTopic.name(), event);
    }
}
