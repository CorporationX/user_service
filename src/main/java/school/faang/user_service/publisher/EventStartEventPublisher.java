package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.EventStartEvent;

@Component
@RequiredArgsConstructor
public class EventStartEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic eventStartTopic;

    public void publish(EventStartEvent event) {
        redisTemplate.convertAndSend(eventStartTopic.getTopic(), event);
    }
}
