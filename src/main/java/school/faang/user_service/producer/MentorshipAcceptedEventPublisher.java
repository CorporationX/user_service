package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.MentorshipAcceptedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic mentorshipAcceptedEventTopic;

    public void publish(MentorshipAcceptedEvent event) {
        kafkaTemplate.send(mentorshipAcceptedEventTopic.name(), event);
    }
}
