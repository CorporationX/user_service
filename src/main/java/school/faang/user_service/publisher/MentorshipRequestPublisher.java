package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestEvent;

@Component
@RequiredArgsConstructor
public class MentorshipRequestPublisher implements MessagePublisher<MentorshipRequestEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(MentorshipRequestEvent message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
