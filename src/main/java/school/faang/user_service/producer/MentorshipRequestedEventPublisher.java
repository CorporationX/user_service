package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.MentorshipRequestedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic mentorshipRequestTopic;

    public void publish(MentorshipRequestedEvent event) {
        kafkaTemplate.send(mentorshipRequestTopic.name(), event);
    }
}
