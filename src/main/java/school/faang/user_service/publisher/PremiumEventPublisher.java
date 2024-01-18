package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PremiumEvent;

@Component
@RequiredArgsConstructor
public class PremiumEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.premium_channel.name}")
    private String premiumChannelName;

    public void publish(PremiumEvent event){
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(premiumChannelName, jsonEvent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
