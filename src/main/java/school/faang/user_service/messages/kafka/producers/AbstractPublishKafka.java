package school.faang.user_service.messages.kafka.producers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import school.faang.user_service.exception.KafkaSendMessageException;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPublishKafka {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    @Async
    public void publish(Object message) {
        log.info("Sending to the Kafka topic {}", message);

        kafkaTemplate.send(topic, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message: {}", message, ex);
                        throw new KafkaSendMessageException(
                                "Error sending message to topic " + topic + ": " + ex.getMessage(), ex
                        );
                    } else {
                        log.info("Message sent successfully: {}", message);
                    }
                });
    }
}