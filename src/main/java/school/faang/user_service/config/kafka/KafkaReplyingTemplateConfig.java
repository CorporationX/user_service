package school.faang.user_service.config.kafka;

import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.CorrelationKey;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;

@Configuration
public class KafkaReplyingTemplateConfig {

    @Value("${spring.kafka.consumer.correlation.premium-price}")
    private String premiumPriceCorrelationId;

    @Value("${spring.kafka.consumer.correlation.premium-payment}")
    private String premiumPaymentCorrelationId;

    @Value("${spring.kafka.consumer.topics.premium.payment.price-response-topic}")
    private String premiumPriceResponseTopic;

    @Value("${spring.kafka.consumer.topics.premium.payment.payment-response-topic}")
    private String premiumPaymentResponseTopic;

    @Value("${spring.kafka.consumer.groups.premium.payment.payment-group}")
    private String premiumPriceResponseGroup;

    @Value("${spring.kafka.consumer.groups.premium.payment.payment-group}")
    private String premiumPaymentResponseGroup;

    @Value("${spring.kafka.consumer.timeout-seconds}")
    private int timeoutSeconds;

    @Bean
    public ConcurrentMessageListenerContainer<String, String> premiumPriceReplyContainer(
            ConsumerFactory<String, String> replyConsumerFactory) {

        ContainerProperties containerProperties = new ContainerProperties(premiumPriceResponseTopic);
        containerProperties.setGroupId(premiumPriceResponseGroup);
        return new ConcurrentMessageListenerContainer<>(replyConsumerFactory, containerProperties);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> premiumPaymentReplyContainer(
            ConsumerFactory<String, String> replyConsumerFactory) {

        ContainerProperties containerProperties = new ContainerProperties(premiumPaymentResponseTopic);
        containerProperties.setGroupId(premiumPaymentResponseGroup);
        return new ConcurrentMessageListenerContainer<>(replyConsumerFactory, containerProperties);
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> premiumPriceReplyingKafkaTemplate(
            ProducerFactory<String, String> producerFactory,
            ConcurrentMessageListenerContainer<String, String> premiumPriceReplyContainer) {

        ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate =
                new ReplyingKafkaTemplate<>(producerFactory, premiumPriceReplyContainer);

        replyingKafkaTemplate.setCorrelationHeaderName(premiumPriceCorrelationId);
        replyingKafkaTemplate.setCorrelationIdStrategy(record -> {
            Header header = record.headers().lastHeader(premiumPriceCorrelationId);
            if (header != null) {
                return new CorrelationKey(header.value());
            } else {
                return null;
            }
        });

        replyingKafkaTemplate.setDefaultReplyTimeout(Duration.ofSeconds(timeoutSeconds));
        replyingKafkaTemplate.setSharedReplyTopic(true);
        return replyingKafkaTemplate;
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> premiumPaymentReplyingKafkaTemplate(
            ProducerFactory<String, String> producerFactory,
            ConcurrentMessageListenerContainer<String, String> premiumPaymentReplyContainer) {

        ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate =
                new ReplyingKafkaTemplate<>(producerFactory, premiumPaymentReplyContainer);

        replyingKafkaTemplate.setCorrelationHeaderName(premiumPaymentCorrelationId);
        replyingKafkaTemplate.setCorrelationIdStrategy(record -> {
            Header header = record.headers().lastHeader(premiumPaymentCorrelationId);
            if (header != null) {
                return new CorrelationKey(header.value());
            } else {
                return null;
            }
        });

        replyingKafkaTemplate.setDefaultReplyTimeout(Duration.ofSeconds(timeoutSeconds));
        replyingKafkaTemplate.setSharedReplyTopic(true);
        return replyingKafkaTemplate;
    }
}
