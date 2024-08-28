package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventStartEvent;

@Component
public class EventStartEventPublisher extends AbstractEventPublisher<EventStartEvent> {
    public EventStartEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper objectMapper,
                                    @Value("${spring.data.redis.channels.event-start.name}") String topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
