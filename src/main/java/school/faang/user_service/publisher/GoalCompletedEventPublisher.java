package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.GoalCompletedEventDto;

@RequiredArgsConstructor
@Component
public class GoalCompletedEventPublisher implements MessagePublisher<GoalCompletedEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.goal-event-channel.name}")
    private String topic;

    @Override
    public void publish(GoalCompletedEventDto goalCompletedEventDto) {
        redisTemplate.convertAndSend(topic, goalCompletedEventDto);
    }
}