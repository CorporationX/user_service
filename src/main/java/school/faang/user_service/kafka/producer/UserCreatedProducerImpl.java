package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.KafkaTopic;
import school.faang.user_service.kafka.dto.user.UserCreated;

@RequiredArgsConstructor
@Component
public class UserCreatedProducerImpl implements UserCreatedProducer {
    private final KafkaTemplate<String, Object> userCreatedProducer;

    public void onUserCreate(UserCreated userCreated) {
        userCreatedProducer.send(KafkaTopic.USER_CREATED.getName(), String.valueOf(userCreated.id()), userCreated);
    }
}
