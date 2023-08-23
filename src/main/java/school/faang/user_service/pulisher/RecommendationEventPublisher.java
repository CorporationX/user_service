package school.faang.user_service.pulisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.EventRecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RecommendationRequestMapper mapper;
    private final ObjectMapper objectMapper;
    private final ChannelTopic topic;

    public void publish(RecommendationRequest recommendationRequest) {
        EventRecommendationRequestDto event = mapper.toEventDto(recommendationRequest);
        event.setReceivedAt(LocalDateTime.now());
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }
}
