package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.event.redis.Event;

@Data
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T extends Event> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic topic;
    
    public void convertAndSend(T event) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), objectMapper.writeValueAsString(event));
            log.debug("Sent to topic {} message : {}", topic.getTopic(), event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
