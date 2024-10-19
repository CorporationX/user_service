package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.MentorshipAcceptedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipAcceptedEventTopic;

    public void publish(MentorshipAcceptedEvent event) {
        redisTemplate.convertAndSend(mentorshipAcceptedEventTopic.getTopic(), event);
    }
}
