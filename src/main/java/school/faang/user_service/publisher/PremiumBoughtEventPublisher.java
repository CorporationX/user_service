package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.PremiumBoughtEvent;

@Component
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher implements MessagePublisher<PremiumBoughtEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topicForPremiumBoughtEvent;
    private final ObjectMapper objectMapper;

    public void publish(PremiumBoughtEvent event) throws JsonProcessingException {
        String jsonEvent = objectMapper.writeValueAsString(event);
        redisTemplate.convertAndSend(topicForPremiumBoughtEvent.getTopic(), jsonEvent);
    }
}