package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.kafka.KafkaProducerService;
import school.faang.user_service.exception.EventSerializationException;

@RequiredArgsConstructor
public abstract class AbstractEventPublisher<E> {
    private final ObjectMapper objectMapper;
    private final KafkaProducerService kafkaProducerService;

    private final String topic;

    public void publish(E event) {
        String jsonEvent = serializeEvent(event);
        kafkaProducerService.sendMessage(topic, jsonEvent);
    }

    private String serializeEvent(E event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("An error occurred while serializing the event");
        }
    }
}
