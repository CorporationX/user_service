package school.faang.user_service.publisher.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.profile.ProfileViewEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher implements MessagePublisher<ProfileViewEvent> {

    @Value("${spring.data.channel.profile_view.name}")
    private String redisChannel;

    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final NewTopic profileViewTopic;

    @Override
    public void publish(ProfileViewEvent event) {
        publishToRedis(event);
        publishToKafka(event);
    }

    private void publishToRedis(ProfileViewEvent event) {
        redisTemplate.convertAndSend(redisChannel, event);
        log.info("Published profile view event to Redis - {}: {}", redisChannel, event);
    }

    private void publishToKafka(ProfileViewEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(profileViewTopic.name(), message);
            log.info("Published profile view event to Kafka - {}: {}", profileViewTopic.name(), message);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing profile view event", e);
        }
    }
}
