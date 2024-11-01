package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.ProjectFollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectSubscriptionPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic followProjectTopic;

    public void publish(ProjectFollowerEvent projectFollowerEvent){
        kafkaTemplate.send(followProjectTopic.name(), projectFollowerEvent);
    }
}
