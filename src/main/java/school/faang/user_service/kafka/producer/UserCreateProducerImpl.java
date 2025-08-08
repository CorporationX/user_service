package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.dto.user.UserCreate;

@RequiredArgsConstructor
@Component
public class UserCreateProducerImpl implements UserCreateProducer {
    private final KafkaTemplate<String, Object> userCreatedProducer;

    public void onUserCreate(UserCreate userCreate) {
        userCreatedProducer.send("user.create", String.valueOf(userCreate.id()), userCreate);
    }
}
