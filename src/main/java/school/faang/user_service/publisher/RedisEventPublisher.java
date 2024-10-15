package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.config.RedisConfig.MessagePublisher;
import school.faang.user_service.exception.EventPublishingException;

@Slf4j
@RequiredArgsConstructor
public abstract class RedisEventPublisher<T> implements MessagePublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic channelTopic;

    public void publish(T redisEvent) {
        try {
            String message = objectMapper.writeValueAsString(redisEvent);
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
        } catch (JsonProcessingException exception) {
            String errorMessage = "Failed to serialize event: " + redisEvent;
            log.error(errorMessage, exception);
            throw new EventPublishingException(errorMessage, exception);
        } catch (Exception exception) {
            String errorMessage = "An unexpected error occurred while publishing event: " + redisEvent;
            log.error(errorMessage, exception);
            throw new EventPublishingException(errorMessage, exception);
        }
    }
}

