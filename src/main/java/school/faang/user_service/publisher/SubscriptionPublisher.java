package school.faang.user_service.publisher;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.kafka.KafkaProperties;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.mapper.FollowerEventMapper;
import school.faang.user_service.protobuf.generate.FollowerEventProto;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class SubscriptionPublisher implements EventPublisher<FollowerEvent> {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final FollowerEventMapper followerEventMapper;

    private String topicName;

    @PostConstruct
    public void initTopicName() {
        topicName = kafkaProperties.getTopics().get("follower").getName();
    }

    @Override
    public void publish(FollowerEvent event) {
        log.info("Publishing follower event with date: {}", event.getEventTime());
        FollowerEventProto.FollowerEvent protoEvent = followerEventMapper.toFollowerEvent(event);
        kafkaTemplate.send(topicName, protoEvent.toByteArray());
        log.info("Published follower event with date: {}", event.getEventTime());
    }
}
