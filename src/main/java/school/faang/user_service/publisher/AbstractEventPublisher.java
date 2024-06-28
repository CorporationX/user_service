package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> implements MessagePublisher<T> {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ChannelTopic topic;
    protected final ObjectMapper objectMapper;

    @Override
    public void publish(T event) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException was thrown", e);
            throw new RuntimeException("Failed to serialized message", e);
        }
    }
}
