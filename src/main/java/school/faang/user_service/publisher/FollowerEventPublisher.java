package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.FollowerEvent;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    @Qualifier("followerEventTopic")
    private final ChannelTopic topic;


    public void publish(FollowerEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
