package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public <T> void send(String topic, String payload, Headers headers) {
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, null,
                key,
                payload,
                headers);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message with key: {}, topic: {}", record.key(), topic);
            } else {
                log.error("Unable to sent message with key: {}", record.key(), ex);
            }
        });
    }
}
