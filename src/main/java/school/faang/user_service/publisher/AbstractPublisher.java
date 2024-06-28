package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class AbstractPublisher<T> implements MessagePublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    protected ChannelTopic channelTopic;

    @Override
    public void publish(T message) {
        log.info("Publishing message on Redis channel {} with content: {}", channelTopic.getTopic(), message);
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
