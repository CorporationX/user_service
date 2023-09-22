package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventStartEventDto;

@Component
public class EventStartEventPublisher extends AbstractEventPublisher<EventStartEventDto> {
    private final ChannelTopic topicStartEvent;

    @Autowired
    public EventStartEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic topicStartEvent) {
        super(redisTemplate, objectMapper);
        this.topicStartEvent = topicStartEvent;
    }

    public void publish(EventStartEventDto event) {
        publishInTopic(topicStartEvent, event);
    }
}
