package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.MentorshipRequestedEvent;


@Service
@RequiredArgsConstructor

public class MentorshipRequestedEventPublisherImpl implements MentorshipRequestedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    public void publish(ChannelTopic topic, MentorshipRequestedEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
