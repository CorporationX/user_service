package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.avro.user.UserCreate;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserCreateProducerImpl implements UserCreateProducer {
    @Value("${spring.kafka.topics.user-create.name}")
    private String topic;

    private final KafkaTemplate<String, UserCreate> producer;

    public void onUserCreate(UserCreate dto) {
        log.info("User create event, data: {}", dto);
        producer.send(topic, String.valueOf(dto.getId()), dto);
    }
}
