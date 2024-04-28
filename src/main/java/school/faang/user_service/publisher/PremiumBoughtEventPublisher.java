package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.UserPremiumBoughtEvent;

@Component
public class PremiumBoughtEventPublisher extends AbstractEventPublisher<UserPremiumBoughtEvent> {

    public PremiumBoughtEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic premiumBoughtTopic, ObjectMapper objectMapper) {
        super(redisTemplate, premiumBoughtTopic, objectMapper);
    }
}
