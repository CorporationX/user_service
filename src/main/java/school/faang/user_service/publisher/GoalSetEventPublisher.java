package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messagebroker.GoalSetEvent;

@Component
@RequiredArgsConstructor
public class GoalSetEventPublisher implements MessagePublisher<GoalSetEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("{$spring.data.redis.channels.goal_set_channel.name}")
    private ChannelTopic goalSetTopic;

    @Override
    public void publish(GoalSetEvent event) {
        redisTemplate.convertAndSend(goalSetTopic.getTopic(), event);
    }
}