package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher {

    @Value("${spring.data.redis.channel.follower}")
    private String followerTopic;

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    public void publish(FollowerEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(followerTopic, json);
            log.info("Published FollowerEvent: {} to '{}'", json, followerTopic);
        } catch (JsonProcessingException e) {
            log.error("Error serializing FollowerEvent: {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
