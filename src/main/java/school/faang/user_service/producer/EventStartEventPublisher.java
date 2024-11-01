package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.EventStartEvent;

@Component
@RequiredArgsConstructor
public class EventStartEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic eventStartTopic;

    public void publish(EventStartEvent event) {
        kafkaTemplate.send(eventStartTopic.name(), event);
    }
}
