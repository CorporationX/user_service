package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.event.SkillAcquireEvent;

@RequiredArgsConstructor
@Component
@Slf4j
public class SkillAcquireEventPublisher implements EventPublisher<SkillAcquireEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
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
    public void publish(SkillAcquireEvent event) {
        redisTemplate.convertAndSend(redisProperties.getChannel().getSkillChannel(), event);
        log.info("Message sent to channel: {}", redisProperties.getChannel().getSkillChannel());
    }
}
