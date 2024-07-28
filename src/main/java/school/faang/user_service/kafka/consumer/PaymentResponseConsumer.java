package school.faang.user_service.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import school.faang.user_service.client.paymentService.model.PaymentResponse;
import school.faang.user_service.service.PaymentService;

/**
 * @author Evgenii Malkov
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentResponseConsumer {

    private final ObjectMapper mapper;
    private final PaymentService paymentService;

    @KafkaListener(topics = "${kafka-topic.consumer.payment-response}",
            groupId = "${spring.kafka.consumer.group-id}")
    @RetryableTopic(
            backoff = @Backoff(value = 5000L),
            attempts = "${spring.kafka.retry.attempts}",
            include = Exception.class
    )
    public void handle(Message<String> message, Acknowledgment acknowledgment) {
        String key = message.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class);
        PaymentResponse paymentResponse = parseResponse(message.getPayload());
        if (paymentResponse == null) {
            log.error("Failed parse paymentResponse, key: {}", key);
        }
        log.info("Received message by requestId: {}, status: {}",
                paymentResponse.requestId(), paymentResponse.status());

        paymentService.handlePaymentResponse(paymentResponse);
        acknowledgment.acknowledge();
    }

    @Nullable
    private PaymentResponse parseResponse(String data) {
        try {
            return mapper.readValue(data, PaymentResponse.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
