package school.faang.user_service.service.event;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.EventStartEvent;

@Component
@RequiredArgsConstructor
public class EventStartEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;


    public void publish(EventStartEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
