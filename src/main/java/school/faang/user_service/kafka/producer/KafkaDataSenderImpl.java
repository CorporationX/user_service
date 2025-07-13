package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.Event;
import school.faang.user_service.kafka.events.AnalyticsEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDataSenderImpl implements DataSender {
    private final KafkaTemplate<String, Object> kafkaTemplateJson;

    @Override
    public void send(KafkaTopics.Topic topic, Event event) {
        log.info("KafkaDataSenderImpl: preparing for sending event: {}", event.toString());
        kafkaTemplateJson.send(topic.getName(), event)
                .whenComplete((record, ex) -> {
                    if (ex == null) {
                        log.info("KafkaDataSenderImpl: successfully sent '{}' with id {}, topic {}, partition = {}, " +
                                        "offset ={}",
                                event.getClass().getSimpleName(),
                                event.getId(),
                                topic,
                                record.getRecordMetadata().partition(),
                                record.getRecordMetadata().offset());
                    } else {
                        log.warn("KafkaDataSenderImpl: {} with id {} has not been sent",
                                event.getClass().getSimpleName(), event.getId(), ex);
                    }
                });
    }

    @Override
    public void send(KafkaTopics.Topic topic, AnalyticsEvent analyticsEvent) {
        kafkaTemplateJson.send(topic.getName(), analyticsEvent)
                .whenComplete((record, ex) -> {
                    if (ex == null) {
                        log.info("Sent analytics event with id {}, topic {}, partition = {}, offset ={}",
                                analyticsEvent.getId(),
                                topic,
                                record.getRecordMetadata().partition(),
                                record.getRecordMetadata().offset());
                    } else {
                        log.warn("Analytics event with id {} has not been sent", analyticsEvent.getId(), ex);
                    }
                });
    }

    @Override
    public void send(KafkaTopics.Topic topic, List<Long> ids) {
        kafkaTemplateJson.send(topic.getName(), ids)
                .whenComplete((record, ex) -> {
                    if (ex == null) {
                        log.info("Sent ids with topic {}, partition = {}, offset ={}, size ={}",
                                topic,
                                record.getRecordMetadata().partition(),
                                record.getRecordMetadata().offset(),
                                ids.size());
                    } else {
                        log.warn("RecordIds list with size {} has not been sent", ids.size(), ex);
                    }
                });
    }
}


