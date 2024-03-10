package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Data
public abstract class AbstractEventPublisher<T> {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    protected void publishInTopic(T event, String channelTopicName) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize event to json");
        }
        redisTemplate.convertAndSend(new ChannelTopic(channelTopicName).getTopic(), json);
    }
}
