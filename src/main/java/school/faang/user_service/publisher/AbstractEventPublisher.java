package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public abstract class AbstractEventPublisher<T> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private ChannelTopic topic;
    
    public AbstractEventPublisher(ChannelTopic topic) {
        this.topic = topic;
    }
    
    public void convertAndSend(T event) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), objectMapper.writeValueAsString(event));
            log.debug("Sent to topic {} message : {}", topic.getTopic(), event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
