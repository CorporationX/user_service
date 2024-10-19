package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.GoalCompletedDto;

@RequiredArgsConstructor
@Component
public class GoalFinishedPublisher {
    private RedisTemplate<String, Object> redisTemplate;

    public void publish(GoalCompletedDto goalCompletedDto, String channel) {
        redisTemplate.convertAndSend(channel, goalCompletedDto);
    }
}
