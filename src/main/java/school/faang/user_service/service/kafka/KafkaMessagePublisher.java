package school.faang.user_service.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessagePublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String channel, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(channel, json);
            log.info(channel + " notification was published to kafka. {}", json);
        } catch (JsonProcessingException e) {
            log.error(channel + " notification failed.");
            throw new RuntimeException(e);
        }
    }
}
