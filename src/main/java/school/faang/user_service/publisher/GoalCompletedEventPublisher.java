package school.faang.user_service.publisher;

import school.faang.user_service.dto.event.GoalCompletedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channel.goal-completed}")
    private String goalCompletedChannel;

    public void publishGoalCompletedEvent(GoalCompletedEvent event) {
        try {
            String eventMessage = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(goalCompletedChannel, eventMessage);
            log.info("Goal completed event published for user {} and goal {}", event.getUserId(), event.getGoalId());
        } catch (JsonProcessingException e) {
            log.error("Error serializing goal completed event", e);
            throw new RuntimeException("Failed to publish goal completed event", e);
        }
    }
}