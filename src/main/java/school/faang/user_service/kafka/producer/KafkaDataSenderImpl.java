package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.Event;
import school.faang.user_service.kafka.events.AnalyticsEvent;
import school.faang.user_service.kafka.events.RecommendationEvent;
import school.faang.user_service.kafka.events.FollowerEvent;
import school.faang.user_service.kafka.events.ProfileViewEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDataSenderImpl implements DataSender {
    private final KafkaTemplate<String, Object> kafkaTemplateJson;

    @Override
    public void send(String topic, Event event) {
        log.info("KafkaDataSenderImpl: preparing for sending event: {}", event.toString());
        kafkaTemplateJson.send(topic, event)
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
    public void send(String topic, AnalyticsEvent analyticsEvent) {
        kafkaTemplateJson.send(topic, analyticsEvent)
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
    public void send(String topic, List<Long> ids) {
        kafkaTemplateJson.send(topic, ids)
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

    @Override
    public void send(String topic, RecommendationEvent recommendationEvent) {
        kafkaTemplateJson.send(topic, recommendationEvent)
                .whenComplete((record, ex) -> {
                    if (ex == null) {
                        log.info("Sent recommendation event with topic {}, partition = {}, offset ={}",
                                topic,
                                record.getRecordMetadata().partition(),
                                record.getRecordMetadata().offset());
                    } else {
                        log.warn("Recommendation event with id {} has not been sent",
                                recommendationEvent.getId(), ex);
                    }
                });
    }

    public void send(String topic, FollowerEvent followerEvent) {
        kafkaTemplateJson.send(topic, followerEvent)
                .whenComplete((record, ex) -> {
                    if (ex == null) {
                        log.info("Published FollowerEvent for follower={}, targetType={}, targetId={} → topic={}, partition={}, offset={}",
                                followerEvent.getFollowerId(),
                                followerEvent.getTargetType(),
                                followerEvent.getTargetId(),
                                topic,
                                record.getRecordMetadata().partition(),
                                record.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to publish FollowerEvent {}, reason:", followerEvent, ex);
                    }
                });
    }
}


