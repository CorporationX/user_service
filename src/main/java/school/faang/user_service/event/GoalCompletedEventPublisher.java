package school.faang.user_service.event;

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

    @Value("${spring.data.redis.channel.goal-completing-channel}")
    private String goalCompletedTopic;

    public void publish(GoalCompletedEvent event) {
        redisTemplate.convertAndSend(goalCompletedTopic, event);
    }
}
