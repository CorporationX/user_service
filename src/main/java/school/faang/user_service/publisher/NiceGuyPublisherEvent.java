package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.NiceGuyEvent;

@Component
@RequiredArgsConstructor
public class NiceGuyPublisherEvent {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic niceGuyAchievementTopic;

    public void publish(NiceGuyEvent event) {
        redisTemplate.convertAndSend(niceGuyAchievementTopic.getTopic(), event);
    }
}
