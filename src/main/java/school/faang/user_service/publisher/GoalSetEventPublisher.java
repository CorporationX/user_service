package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messagebroker.GoalSetEvent;

@Component
@Slf4j
public class GoalSetEventPublisher extends MessagePublisher<GoalSetEvent> {

    public GoalSetEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    public void publish(GoalSetEvent goalSetEvent) {
        //@Value("{$spring.data.redis.channels.goal_set_channel.name}")
        String goalSetTopic = "goal_set_channel";
        convertAndSend(goalSetTopic, goalSetEvent);
        log.info("Goal set event published user id: " + goalSetEvent.getUserId());
    }
}