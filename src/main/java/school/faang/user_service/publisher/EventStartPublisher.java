package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventStartDto;

@Component
public class EventStartPublisher extends AbstractPublisher<EventStartDto> {

    public EventStartPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
