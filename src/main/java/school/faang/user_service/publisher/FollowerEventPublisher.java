package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEventDto;

@Slf4j
@Component
public class FollowerEventPublisher implements MessagePublisher<FollowerEventDto> {
    private final RedisTemplate<String, FollowerEventDto> redisTemplate;
    private final ChannelTopic followerEventTopic;

    public FollowerEventPublisher(RedisTemplate<String, FollowerEventDto> redisTemplate,
                                  @Qualifier("followerEventChannel") ChannelTopic followerEventTopic) {
        this.redisTemplate = redisTemplate;
        this.followerEventTopic = followerEventTopic;
    }

    @Override
    @Retryable(retryFor = RuntimeException.class,
            backoff = @Backoff(delayExpression = "${spring.data.redis.publisher.delay}"))
    public void publish(FollowerEventDto followerEventDto) {
        try {
            redisTemplate.convertAndSend(followerEventTopic.getTopic(), followerEventDto);
            log.debug("Published follower event: {}", followerEventDto);
        } catch (Exception e) {
            log.error("Failed to publish follower event: {}", followerEventDto, e);
            throw new RuntimeException(e);
        }
    }
}
