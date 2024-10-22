package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.ProfilePicEvent;
import school.faang.user_service.mapper.ProfilePicEventMapper;
import school.faang.user_service.protobuf.generate.ProfilePicEventProto;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfilePicEventPublisher implements EventPublisher<ProfilePicEvent> {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final ProfilePicEventMapper profilePicEventMapper;

    @Value("${spring.kafka.topics.profile_pic.name}")
    private String topicName;

    @Override
    public void publish(ProfilePicEvent event) {
        log.info("Publishing follower event with date: {}", event.getEventTime());
        ProfilePicEventProto.ProfilePicEvent protoEvent = profilePicEventMapper.toProto(event);
        kafkaTemplate.send(topicName, protoEvent.toByteArray());
        log.info("Published follower event with date: {}", event.getEventTime());
    }
}
