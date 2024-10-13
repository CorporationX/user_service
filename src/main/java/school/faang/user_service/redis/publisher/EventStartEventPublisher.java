package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis.event.EventStartEvent;

@Component
public class EventStartEventPublisher extends AbstractEventPublisher<EventStartEvent> {
    public EventStartEventPublisher(RedisTemplate<String, Object> redisTemplate, Topic eventStartTopic,
                                    ObjectMapper objectMapper) {
        super(redisTemplate, eventStartTopic, objectMapper);
    }
}
