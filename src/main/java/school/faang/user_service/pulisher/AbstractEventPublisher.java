package school.faang.user_service.pulisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.SerializeJsonException;

@Component
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    protected void publishInTopic(ChannelTopic topic, T event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new SerializeJsonException("Cannot serialize event to json");
        }
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }
}
