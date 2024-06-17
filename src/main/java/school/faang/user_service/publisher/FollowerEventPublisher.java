package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEvent;

@Slf4j
@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEvent> {
    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ChannelTopic topic, ObjectMapper objectMapper) {
        super(redisTemplate, topic, objectMapper);
    }
}
