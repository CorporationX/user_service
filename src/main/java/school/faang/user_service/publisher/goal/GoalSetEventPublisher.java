package school.faang.user_service.publisher.goal;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.event.goal.GoalSetEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Component
@RequiredArgsConstructor
public class GoalSetEventPublisher implements MessagePublisher<GoalSetEvent>{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic goalSetTopic;

    public void publish(GoalSetEvent event) {
        redisTemplate.convertAndSend(goalSetTopic.getTopic(), event);
    }
}
