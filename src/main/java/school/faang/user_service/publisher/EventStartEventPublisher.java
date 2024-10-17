package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class EventStartEventPublisher {

    private final RedisTemplate<String, Object> restTemplate;
    private final ChannelTopic eventStartTopic;

    public void publish(String message) {
        restTemplate.convertAndSend(eventStartTopic.getTopic(), message);
    }
}
