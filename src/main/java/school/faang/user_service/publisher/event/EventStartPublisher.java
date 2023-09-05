package school.faang.user_service.publisher.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.publisher.AbstractPublisher;

@Component
public class EventStartPublisher extends AbstractPublisher<EventStartDto> {
    public EventStartPublisher(ObjectMapper jsonMapper,
                               RedisTemplate<String, Object> redisTemplate,
                               @Value("${spring.data.redis.channels.event_channel.name}") String channel) {
        super(jsonMapper,redisTemplate, channel);
    }
}
