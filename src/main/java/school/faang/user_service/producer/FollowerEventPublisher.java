package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.FollowerEvent;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic followerEventTopic;

    public void publish(FollowerEvent event) {
        kafkaTemplate.send(followerEventTopic.name(), event);
    }
}
