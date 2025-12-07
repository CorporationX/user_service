package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.premium.PremiumBoughtEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic premiumBoughtTopic;

    public void publish(PremiumBoughtEvent event) {
        try {
            redisTemplate.convertAndSend(premiumBoughtTopic.getTopic(), event);
            log.info("Published PremiumBoughtEvent for user: {}, amount: {}, duration: {} months",
                    event.getUserId(), event.getPaymentAmount(), event.getSubscriptionDurationMonths());
        } catch (Exception e) {
            log.error("Failed to publish PremiumBoughtEvent for user: {}", event.getUserId(), e);
        }
    }
}