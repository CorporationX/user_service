package school.faang.user_service.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
public abstract class KafkaAbstractProducer {

    protected final KafkaTemplate<String, Object> kafkaTemplate;

    public abstract void sendMessage(Object message);

    public void handleFuture(CompletableFuture<SendResult<String, Object>> future) {
        future.whenComplete(((sendResult, throwable) -> {
            String key = sendResult.getProducerRecord().key();
            Object value = sendResult.getProducerRecord().value();
            if (throwable != null) {
                handleFailure(key, value, throwable);
            } else {
                handleSuccess(key, value, sendResult);
            }
        }));
    }

    public void handleSuccess(String key, Object value, SendResult<String, Object> sendResult) {
        log.info("Message sent successfully for the key: {} and the value: {}, partition is: {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    public void handleFailure(String key, Object value, Throwable throwable) {
        log.error("Error sending message and exception is {}", throwable.getMessage(), throwable);
    }
}
