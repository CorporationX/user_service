package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.messaging.events.GoalCompletedEvent;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher implements MessagePublisher<GoalCompletedEvent>{
    @Value("${spring.data.redis.channels.goal_completed_channel.name}")
    private String topic;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(GoalCompletedEvent goalCompletedEvent){
        String json = null;
        try {
            json = objectMapper.writeValueAsString(goalCompletedEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(topic, json);
    }
}
