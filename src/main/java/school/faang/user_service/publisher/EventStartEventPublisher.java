package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.RetryProperties;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.EventStartEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStartEventPublisher implements EventPublisher<EventStartEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RetryProperties retryProperties;
    private final RedisProperties redisProperties;

    @Override
    @Retryable(retryFor = Exception.class,
            maxAttemptsExpression = "#{@retryProperties.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "#{@retryProperties.initialDelay}",
                    multiplierExpression = "#{@retryProperties.multiplier}",
                    maxDelayExpression = "#{@retryProperties.maxDelay}"
            )
    )
    public void publish(EventStartEvent event) {
        redisTemplate.convertAndSend(redisProperties.channel().eventStartEventChannel(), event);
        log.info("Event start notification sent: eventId={}, channel='{}'",
                event.eventId(),
                redisProperties.channel().eventStartEventChannel()
        );
    }

    @Override
    public Class<EventStartEvent> getEventClass() {
        return EventStartEvent.class;
    }
}
