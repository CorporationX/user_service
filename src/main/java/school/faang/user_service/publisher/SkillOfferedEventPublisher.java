package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.SkillOfferEventDto;
import school.faang.user_service.exception.SerializeJsonException;

@Component
@RequiredArgsConstructor
public class SkillOfferedEventPublisher {

    public final RedisTemplate<String, Object> redisTemplate;
    public final ObjectMapper objectMapper;
    public final ChannelTopic topicSkillOffer;

    public void publish(SkillOfferEventDto event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new SerializeJsonException("Failed to serialize event");
        }
        redisTemplate.convertAndSend(topicSkillOffer.getTopic(), json);
    }
}
