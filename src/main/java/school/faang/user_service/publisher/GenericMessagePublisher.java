package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class GenericMessagePublisher<T> implements MessagePublisher<T>{
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ObjectMapper objectMapper;

    @Override
    public void publish(T t) {
        String message;
        try {
            message = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.info("Method: publish {}", e.getMessage());
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(getTopic().toString(), message);
        log.info("Published message: {} to channel: {}", message, getTopic());
    }

    protected abstract ChannelTopic getTopic();
}
