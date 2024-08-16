package school.faang.user_service.messaging.publisher.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.event.recommendation.RecommendationEvent;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.messaging.publisher.EventPublisher;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher implements EventPublisher<RecommendationEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(RecommendationEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Published Recommendation event: {}", message);
            redisTemplate.convertAndSend(recommendationTopic.getTopic(), message);
        } catch (JsonProcessingException e) {
            log.error(ExceptionMessages.SERIALIZATION_ERROR + event, e);
            throw new IllegalArgumentException(ExceptionMessages.SERIALIZATION_ERROR + event, e);
        } catch (Exception e) {
            log.error(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
            throw new IllegalArgumentException(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
        }
    }

    public void toEventAndPublish(RecommendationDto recommendationDto) {
        publish(RecommendationEvent.builder()
                .recommendationId(recommendationDto.getId())
                .authorId(recommendationDto.getAuthorId())
                .receiverId(recommendationDto.getReceiverId())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
