package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessageMentorshipRequestsPublisher implements MessagePublisher<MentorshipRequestEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(MentorshipRequestEvent message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
