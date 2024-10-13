package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.MessagePublishingException;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class AbstractMessagePublisher<T> implements MessagePublisher<T> {

    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ObjectMapper objectMapper;

    @Override
    public void publish(T eventMessage) {
        try {
            String message = objectMapper.writeValueAsString(eventMessage);
            redisTemplate.convertAndSend(getTopic().toString(), message);

            log.info("Successfully published message: {} to channel: {}", message, getTopic().getTopic());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize eventMessage of type {} for channel: {}. Error: {}",
                    eventMessage.getClass().getSimpleName(), getTopic().getTopic(), e.getMessage(), e);
            throw new MessagePublishingException("Failed to publish message to Redis channel", e);
        }
    }

    protected abstract ChannelTopic getTopic();
}

