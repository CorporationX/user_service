package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.PremiumBoughtEvent;

@Component
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic premiumBoughtTopic;

    public void publish(PremiumBoughtEvent event) {
        redisTemplate.convertAndSend(premiumBoughtTopic.getTopic(), event);
    }
}