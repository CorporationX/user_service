package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.MentorshipStartEvent;

@Component
@RequiredArgsConstructor
public class MentorshipStartEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic mentorshipStartTopic;

    public void publish(MentorshipStartEvent event) {
        kafkaTemplate.send(mentorshipStartTopic.name(), event);
    }
}
