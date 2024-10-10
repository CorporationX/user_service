package school.faang.user_service.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.event.GoalCompletedEventDto;

@Component
public class GoalCompletedEventPublisher extends AbstractEventPublisher<GoalCompletedEventDto> {

    private final ChannelTopic goalCompletedEventTopic;

    public GoalCompletedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                       ChannelTopic topic) {
        super(redisTemplate);
        this.goalCompletedEventTopic = topic;
    }

    public void sendEvent(GoalCompletedEventDto event) {
        publish(goalCompletedEventTopic, event);
    }
}
