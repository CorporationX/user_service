package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.PremiumBoughtEvent;

@Component
public class PremiumBoughtEventPublisher extends AbstractPublisher<PremiumBoughtEvent> {
    public PremiumBoughtEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper jsonMapper, @Value("${spring.data.redis.channels.premium_bought_channel.name}") String premiumBoughtChannelTopic) {
        super(redisTemplate, jsonMapper, premiumBoughtChannelTopic);
    }
}
