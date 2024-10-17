package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.MentorshipStartEvent;

@Component
@RequiredArgsConstructor
public class MentorshipStartEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipStartTopic;

    public void publish(MentorshipStartEvent event) {
        redisTemplate.convertAndSend(mentorshipStartTopic.getTopic(), event);
    }
}
