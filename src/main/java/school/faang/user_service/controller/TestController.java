package school.faang.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.model.dto.GoalCompletedEvent;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class TestController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final GoalCompletedEventPublisher goalCompletedRedisEventPublisher;

    @Value("${redis.channels.goal-completed-channel}")
    private String goalCompletedChannel;

    @PostMapping("/goal-completed")
    public void completeGoal(@RequestBody GoalCompletedEvent event) {
        goalCompletedRedisEventPublisher.publish(event);
    }
}