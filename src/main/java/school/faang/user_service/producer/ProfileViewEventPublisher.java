package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.ProfileViewEventDto;

@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic profileViewTopic;

    public void publish(ProfileViewEventDto profileEvent) {
        kafkaTemplate.send(profileViewTopic.name(), profileEvent);
    }
}
