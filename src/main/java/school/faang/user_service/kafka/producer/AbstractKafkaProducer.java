package school.faang.user_service.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;

public abstract class AbstractKafkaProducer<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    protected AbstractKafkaProducer(KafkaTemplate<String, String> kafkaTemplate,
                                    ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    protected abstract String getTopic();

    public void sendEvent(T event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(getTopic(), json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event to JSON", e);
        }
    }
}
