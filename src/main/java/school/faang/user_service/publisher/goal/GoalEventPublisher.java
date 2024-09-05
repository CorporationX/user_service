package school.faang.user_service.publisher.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.goal.GoalSetEvent;
import school.faang.user_service.publisher.AbstractEventPublisher;

@Service
public class GoalEventPublisher extends AbstractEventPublisher<GoalSetEvent> {

    public GoalEventPublisher(RedisTemplate<String, Object> redisTemplate, @Qualifier("goalTopic") ChannelTopic topic, ObjectMapper objectMapper) {
        super(redisTemplate, topic, objectMapper);
    }

    public void sendEvent(GoalSetEvent goalSetEvent){
        publish(goalSetEvent);
    }
}
