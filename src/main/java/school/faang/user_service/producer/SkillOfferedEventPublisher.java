package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.SkillOfferedEvent;

@Component
@RequiredArgsConstructor
public class SkillOfferedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic skillOfferedTopic;

    public void publish(SkillOfferedEvent event) {
        kafkaTemplate.send(skillOfferedTopic.name(), event);
    }
}
