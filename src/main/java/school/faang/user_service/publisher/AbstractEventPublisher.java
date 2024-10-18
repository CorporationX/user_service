package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> implements MessagePublisher<T> {

    private final RedisTemplate<String, T> redisTemplate;
    private final ChannelTopic channelTopic;

    @Retryable(retryFor = {RuntimeException.class},
            backoff = @Backoff(delayExpression = "${spring.data.redis.publisher.delay}"))
    @Override
    public void publish(T event) {
        try {
            redisTemplate.convertAndSend(channelTopic.getTopic(), event);
            log.debug("Published event: {}", event);
        } catch (Exception e) {
            log.error("Failing to publish event {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
