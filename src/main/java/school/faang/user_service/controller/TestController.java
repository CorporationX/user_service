package school.faang.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.publisher.GoalCompletedRedisEventPublisher;
import school.faang.user_service.redis_event.GoalCompletedRedisEvent;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class TestController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final GoalCompletedRedisEventPublisher goalCompletedRedisEventPublisher;

    @Value("${spring.data.redis.channels.goal-completed-channel.name}")
    private String goalCompletedChannel;

    @PostMapping("/goal-completed")
    public void completeGoal(@RequestBody GoalCompletedRedisEvent event) {
        goalCompletedRedisEventPublisher.publish(event);
    }
}