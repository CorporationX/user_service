package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;


@RequiredArgsConstructor
public class AbstractEventPublisher<E> implements EventPublisher<E> {
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ChannelTopic topic;

    @Override
    public void publish(E message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
