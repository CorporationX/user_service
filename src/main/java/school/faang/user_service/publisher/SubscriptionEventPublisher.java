package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import school.faang.user_service.dto.event.redis.SubscriptionEvent;

@Component
public class SubscriptionEventPublisher extends AbstractEventPublisher<SubscriptionEvent> {
    public SubscriptionEventPublisher(
        RedisTemplate<String, Object> redisTemplate,
        ObjectMapper objectMapper,
        @Qualifier("subscriptionTopic") ChannelTopic topic
    ) {
        super(redisTemplate, objectMapper, topic);
    }
}
