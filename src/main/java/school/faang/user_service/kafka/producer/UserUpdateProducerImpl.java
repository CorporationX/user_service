package school.faang.user_service.kafka.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.KafkaTopic;
import school.faang.user_service.kafka.dto.EnvelopeMessage;
import school.faang.user_service.kafka.dto.user.update.UserUpdateEvent;

@Component
@RequiredArgsConstructor
public class UserUpdateProducerImpl implements UserUpdateProducer {
    private final KafkaTemplate<String, Object> userCreatedProducer;
    private final ObjectMapper objectMapper;

    @Override
    public void onUserUpdate(UserUpdateEvent dto) {
        JsonNode payloadNode = objectMapper.valueToTree(dto);
        userCreatedProducer.send(
                KafkaTopic.USER_UPDATED.getName(),
                String.valueOf(dto.getId()),
                new EnvelopeMessage(dto.getType(), payloadNode)
        );
    }
}
