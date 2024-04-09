package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.SerializationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.UserPremiumBoughtEvent;

@Component
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic premiumBoughtTopic;
    private final ObjectMapper objectMapper;

    public void publish(UserPremiumBoughtEvent userPremiumBoughtEvent) {
        try {
            redisTemplate.convertAndSend(premiumBoughtTopic.getTopic(), objectMapper.writeValueAsString(userPremiumBoughtEvent));
        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed to serialize user premium bought event", e);
        }
    }
}
