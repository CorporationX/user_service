package school.faang.user_service.publisher.goal;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.goal.GoalSetEvent;
import school.faang.user_service.publisher.AbstractEventPublisher;

@Service
public class GoalEventPublisher extends AbstractEventPublisher<GoalSetEvent> {
    public GoalEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        super(redisTemplate, topic);
    }

    public void sendEvent(GoalSetEvent goalSetEvent){
        publish(goalSetEvent);
    }
}
