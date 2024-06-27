package school.faang.user_service.publisher.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.premium.PremiumBoughtEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher implements MessagePublisher<PremiumBoughtEvent> {

    @Value("${spring.data.redis.channels.premium_bought_channel.name}")
    private String topic;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(PremiumBoughtEvent event) {
        redisTemplate.convertAndSend(topic, event);
        log.info("Published premium bought event - {}:{}", topic, event);
    }
}
