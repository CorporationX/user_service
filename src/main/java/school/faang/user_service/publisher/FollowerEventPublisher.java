package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.event.follower.FollowerEvent;

@Slf4j
@RequiredArgsConstructor
public class FollowerEventPublisher implements MessagePublisher<FollowerEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.data.kafka.channels.follower}")
    private String followerChannel;

    @Override
    public void publish(FollowerEvent event) {
        kafkaTemplate.send(followerChannel, event);
        log.info("Follower event was published {}", event);
    }
}
