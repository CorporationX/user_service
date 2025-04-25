package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.promotion.event.MentorshipStartEvent;

@Component
@RequiredArgsConstructor
public class MentorshipEventPublisher {
    private final RedisTemplate<String, MentorshipStartEvent> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(MentorshipStartEvent startEvent) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), startEvent);
    }
}
