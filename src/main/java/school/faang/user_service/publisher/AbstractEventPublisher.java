package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;


@Slf4j
@RequiredArgsConstructor
public class AbstractEventPublisher<E> implements EventPublisher<E> {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ChannelTopic topic;
    protected final ObjectMapper objectMapper;

    @Override
    public void publish(E event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic.getTopic(), message);
            log.info("Successfully published event: {}", event);
        } catch (JsonProcessingException e) {
            log.error("Error serializing event", e);
            throw new RuntimeException("Error serializing event", e);
        }

    }
}
