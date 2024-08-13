package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.RecommendationEventPublisher;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationSentPublisher implements MessagePublisher<RecommendationEventPublisher> {
    private final ChannelTopic topic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(RecommendationEventPublisher event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic.getTopic(), message);
            log.info("Published recommendation: {}", event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize recommendation: {}", event, e);
            throw new IllegalArgumentException("Failed to serialize recommendation: " + event);
        }
    }
}