package school.faang.user_service.messaging.publisher.recommendationReceived;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.event.recommendationReceived.RecommendationReceivedEvent;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.messaging.publisher.EventPublisher;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationPublisher implements EventPublisher<RecommendationReceivedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationReceivedTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(RecommendationReceivedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(recommendationReceivedTopic.getTopic(), message);
        } catch (Exception e) {
            log.error(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
            throw new IllegalArgumentException(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
        }
    }
}