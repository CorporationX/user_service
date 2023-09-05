package school.faang.user_service.publisher.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.publisher.AbstractPublisher;

@Component
public class EventStartPublisher extends AbstractPublisher<EventStartDto> {
    public EventStartPublisher(RedisTemplate<String, Object> redisTemplate,
                               ObjectMapper jsonMapper,
                               @Value("${spring.data.redis.channels.event_channel.name}") String channel) {
        super(redisTemplate, jsonMapper, channel);
    }
}
