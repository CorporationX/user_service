package integration.kafka;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.premium.PremiumPaymentRequestDto;
import school.faang.user_service.dto.premium.PremiumPaymentResponseDto;
import school.faang.user_service.utils.JsonUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@ActiveProfiles("test")
public class FakePremiumListener {
    private static final String RECEIVED_MESSAGE_FROM_KAFKA = "Received message from kafka: {}";
    public static final String FAILED_TO_ACKNOWLEDGE_KAFKA_MESSAGE = "Failed to acknowledge Kafka message";

    @Value("${spring.kafka.consumer.topics.premium.payment-request-topic}")
    String aaa;

    @Value("${spring.kafka.consumer.correlation.premium-payment}")
    private String premiumPaymentCorrelationId;

    @Value("${spring.kafka.producer.topics.premium.payment.payment-response-topic}")
    private String paymentResponseTopic;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private TestKafkaPublisher testKafkaPublisher;

    @PostConstruct
    public void init() {
        log.info("FakePremiumListener started with topic {}", aaa);
    }

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.premium.payment-request-topic}",
            groupId = "${spring.kafka.consumer.groups.premium.payment-request-group}"
    )
    @Transactional(transactionManager = "kafkaTransactionManager")
    public void premiumPaymentRequestListener(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        String message = record.value();
        log.info(RECEIVED_MESSAGE_FROM_KAFKA, message);

        PremiumPaymentRequestDto premiumPaymentRequest =
                jsonUtils.deserialize(message, PremiumPaymentRequestDto.class);

        PaymentResponseDto paymentResponse = new PaymentResponseDto(PaymentStatus.SUCCESS, 1,
                1, BigDecimal.TEN, CurrencyDto.USD, "message");

        PremiumPaymentResponseDto premiumPaymentResponseDto = PremiumPaymentResponseDto.builder()
                .premiumRequest(premiumPaymentRequest.getPremiumRequest())
                .paymentResponse(paymentResponse)
                .byUser(premiumPaymentRequest.isByUser())
                .build();

        String correlationId = null;
        Header header = record.headers().lastHeader(premiumPaymentCorrelationId);
        if (header != null) {
            correlationId = new String(header.value(), StandardCharsets.UTF_8);
        } else {
            log.warn("No premium-price-correlation-id header found in the request");
        }
        testKafkaPublisher.sendInTransaction(premiumPaymentResponseDto, paymentResponseTopic,
                premiumPaymentCorrelationId, correlationId);

        acknowledgeMessage(acknowledgment);
    }

    private void acknowledgeMessage(Acknowledgment acknowledgment) {
        try {
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error(FAILED_TO_ACKNOWLEDGE_KAFKA_MESSAGE, e);
            throw new RuntimeException(e);
        }
    }
}
