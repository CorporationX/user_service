package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipOfferedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(MentorshipOfferedEvent event) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), event);
    }
}