package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.GoalCompletedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channel.goal_completed}")
    private String channel;

    public void publish(GoalCompletedEvent goalCompletedEvent) throws JsonProcessingException {
        log.info("Publishing goal completed event notification");
        redisTemplate.convertAndSend(channel, goalCompletedEvent);
        log.info("Goal completed event notification sent");
    }
}