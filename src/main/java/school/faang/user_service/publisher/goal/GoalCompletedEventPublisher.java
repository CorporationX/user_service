package school.faang.user_service.publisher.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.GoalCompletedEventDto;
import school.faang.user_service.publisher.MessagePublisher;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher implements MessagePublisher<GoalCompletedEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic goalCompletedEventTopic;

    public void publish(GoalCompletedEventDto message) {
        redisTemplate.convertAndSend(goalCompletedEventTopic.getTopic(), message);
    }
}