package school.faang.user_service.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author Evgenii Malkov
 */
@Component
@Slf4j
public class PaymentResponseConsumer {

    @KafkaListener(topics = "${kafka-topic.consumer.payment-response}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handle(Message<String> message, Acknowledgment acknowledgment) {
        log.info(message.getPayload());
        log.info("Логика сохранения купленного товара");
        acknowledgment.acknowledge();
    }
}
