package school.faang.user_service.message.producer.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.kafka.KafkaConfig;
import school.faang.user_service.message.producer.KeyedMessagePublisher;

@Component(KafkaConfig.KAFKA_PRODUCER_NAME)
@RequiredArgsConstructor
public class KafkaMessageProducer implements KeyedMessagePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(String topic, String key, Object message) {
        kafkaTemplate.send(topic, key, message);
    }

    @Override
    public void send(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }
}
