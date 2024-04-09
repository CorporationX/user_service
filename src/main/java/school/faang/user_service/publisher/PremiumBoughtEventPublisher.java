package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.PremiumBoughtEvent;

@Component
public class PremiumBoughtEventPublisher extends AbstractEventPublisher<PremiumBoughtEvent> {

    @Value("${spring.data.redis.channels.premium_bought_channel.name}")
    private String premiumChannel;

    public PremiumBoughtEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    @Override
    public void publish(PremiumBoughtEvent premiumBoughtEvent) {
        convertAndSend(premiumBoughtEvent, premiumChannel);
    }
}
