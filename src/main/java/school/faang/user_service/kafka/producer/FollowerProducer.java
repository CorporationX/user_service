package school.faang.user_service.kafka.producer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.kafka.FollowerEvent;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowerProducer {
    @Value("${spring.data.kafka.topics.follower-event.topic_name}")
    private String topicName;

    private final KafkaTemplate<String, Object> objectKafkaTemplate;

    public void sendToKafka(@NonNull FollowerEvent followerEvent) {
        String key = LocalDateTime.now().toString();
        objectKafkaTemplate.send(topicName, key, followerEvent);
        log.info("Новый followerEvent от пользователя с id: {} отправлен в Kafka", followerEvent.followerId());
    }
}
