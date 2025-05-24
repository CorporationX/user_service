package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.AnalyticsCreatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDataSenderImpl implements DataSender {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void send(String topic, String key, AnalyticsCreatedEvent analyticsCreatedEvent) {
        kafkaTemplate.send(topic, key, analyticsCreatedEvent)
                .whenComplete((record, ex) -> {
                    if (ex == null) {
                        log.info("Sent analytics event with id {}, key {},  topic {}, partition = {}, offset ={}",
                                analyticsCreatedEvent.getId(),
                                key,
                                topic,
                                record.getRecordMetadata().partition(),
                                record.getRecordMetadata().offset());
                    } else {
                        log.warn("Analytics event with id {} has not been sent", analyticsCreatedEvent.getId(), ex);
                    }
                });
    }
}


