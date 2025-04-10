package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.kafka.KafkaProducerService;
import school.faang.user_service.event.ProfilePicEvent;

@Component
public class ProfilePicEventPublisher extends AbstractEventPublisher<ProfilePicEvent> {

    public ProfilePicEventPublisher(
            ObjectMapper objectMapper,
            KafkaProducerService kafkaProducerService,
            @Value("${spring.kafka.topic.profile-pic-topic.name:profile-pic-topic}")
                    String profilePicTopic) {
        super(objectMapper, kafkaProducerService, profilePicTopic);
    }
}
