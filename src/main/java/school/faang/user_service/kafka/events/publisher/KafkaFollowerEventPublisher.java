package school.faang.user_service.kafka.events.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.events.FollowerEvent;
import school.faang.user_service.kafka.producer.KafkaTopics;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaFollowerEventPublisher implements FollowerEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopics kafkaTopics;

    @Override
    public void publish(FollowerEvent event) {
        kafkaTemplate.send(
                kafkaTopics.getFollowerEventsTopic(),
                event.getFollowerId(),
                event
        ).whenComplete((record, ex) -> {
            if (ex == null) {
                log.info("Published FollowerEvent for follower={}, targetType={}, targetId={} → topic={}, partition={}, offset={}",
                        event.getFollowerId(),
                        event.getTargetType(),
                        event.getTargetId(),
                        kafkaTopics.getFollowerEventsTopic(),
                        record.getRecordMetadata().partition(),
                        record.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish follower event {}, reason:", event, ex);
            }
        });
    }
}
