package school.faang.user_service.redis.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.core.RedisTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.PremiumBoughtEventDto;

@Component
public class PremiumBoughtEventPublisher extends AbstractEventPublisher<PremiumBoughtEventDto> {

    public PremiumBoughtEventPublisher(@Qualifier("eventRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                                       ObjectMapper objectMapper,
                                       ChannelTopic premiumBoughtTopic) {
        super(redisTemplate, objectMapper, premiumBoughtTopic);
    }
}