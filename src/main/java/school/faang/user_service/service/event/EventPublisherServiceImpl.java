package school.faang.user_service.service.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.kafka.KafkaPublishException;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishEvent(@NonNull Object event, @NonNull String eventId, @NonNull String topic) {
        try {
            kafkaTemplate.send(topic, eventId, event);
        } catch (TimeoutException timeoutException) {
            log.error("Timed out publishing: {}", event, timeoutException);
            throw new KafkaPublishException("Timeout while publishing", timeoutException);

        } catch (Exception exception) {
            log.error("Attempt to publish event {} failed", event, exception);
            throw new KafkaPublishException("Event publishing attempt failed", exception);
        }
    }
}
