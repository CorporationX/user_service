package school.faang.user_service.kafka.producer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.data.HeaderName;

import java.nio.charset.StandardCharsets;

/**
 * @author Evgenii Malkov
 */
@Component
@Slf4j
public class PaymentRequestProducer extends AbstractProducer {
    @Value("${kafka-topic.producer.payment-request}")
    private String topic;

    public PaymentRequestProducer(KafkaTemplate<String, String> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendPaymentRequest(String payload) {
        super.send(topic, payload, null);
    }
}
