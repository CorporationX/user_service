package school.faang.user_service.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.dto.EnvelopeMessage;
import school.faang.user_service.kafka.dto.user.update.UserUpdateEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpdateProducerImpl implements UserUpdateProducer {
    @Value("${spring.kafka.topics.user-update.name}")
    private String userUpdateTopic;

    private final KafkaTemplate<String, EnvelopeMessage<UserUpdateEvent>> producer;
    private final ObjectMapper objectMapper;

    @Override
    public void onUserUpdate(UserUpdateEvent dto) {
        log.info("User update event, data: {}", dto);
        producer.send(
                userUpdateTopic,
                String.valueOf(dto.getId()),
                new EnvelopeMessage<>(dto.getType(), dto)
        );
    }
}
