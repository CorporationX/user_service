package school.faang.user_service.redisPublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
@Slf4j
public abstract class EventPublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerChannelTopic;
    private final ObjectMapper objectMapper;

    public void publish(T message) {
        try {
            redisTemplate.convertAndSend(followerChannelTopic.getTopic(), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Error while publishing event to redis message: " + message, e);
            throw new RuntimeException(e);
        }
    }
}
