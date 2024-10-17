package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.PremiumBoughtEvent;

@Service
@Slf4j
public class PremiumBoughtEventPublisher implements MessagePublisher<PremiumBoughtEvent>{
    private final RedisTemplate<String, PremiumBoughtEvent> redisTemplate;
    private final ChannelTopic premiumChannelTopic;

    public PremiumBoughtEventPublisher(RedisTemplate<String, PremiumBoughtEvent> redisTemplate,
                                       @Qualifier("premiumChannel") ChannelTopic premiumChannelTopic
    ) {
        this.redisTemplate = redisTemplate;
        this.premiumChannelTopic = premiumChannelTopic;
    }
    @Override
    @Retryable(
            retryFor = {RuntimeException.class},
            backoff = @Backoff(delayExpression = "${spring.data.redis.publisher.delay}")
    )
    public void publish(PremiumBoughtEvent message) {
        try {
            redisTemplate.convertAndSend(premiumChannelTopic.getTopic(), message);
            log.info("PremiumBought event published: {}", message);
        } catch (Exception e) {
            log.error("PremiumBought event didn't published: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}
