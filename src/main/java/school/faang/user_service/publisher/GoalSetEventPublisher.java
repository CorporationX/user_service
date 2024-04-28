package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messagebroker.GoalSetEvent;

@Component
@Slf4j
public class GoalSetEventPublisher extends AbstractEventPublisher<GoalSetEvent> {
    @Value("{$spring.data.redis.channels.goal_set_channel.name}")
    private ChannelTopic goalSetTopic;

    public GoalSetEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(GoalSetEvent goalSetEvent) {
        convertAndSend(goalSetEvent, goalSetTopic.getTopic());
        log.info("Goal set event published user id: " + goalSetEvent.getUserId());

    }
}