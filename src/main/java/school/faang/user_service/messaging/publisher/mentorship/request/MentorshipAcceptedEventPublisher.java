package school.faang.user_service.messaging.publisher.mentorship.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.RedisEvent;
import school.faang.user_service.messaging.publisher.EventPublisher;

@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher implements EventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(RedisEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
