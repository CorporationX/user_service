package school.faang.user_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

public abstract class AbstractKafkaConsumer<T> {

    private final ObjectMapper objectMapper;
    private final Class<T> targetType;

    protected AbstractKafkaConsumer(ObjectMapper objectMapper, Class<T> targetType) {
        this.objectMapper = objectMapper;
        this.targetType = targetType;
    }

    public void consume(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String eventJson = record.value();
        try {
            T event = objectMapper.readValue(eventJson, targetType);
            processEvent(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            handleError(eventJson, e, acknowledgment);
        }
    }

    protected abstract void processEvent(T event);

    protected abstract void handleError(String eventJson, Exception e, Acknowledgment acknowledgment);
}
