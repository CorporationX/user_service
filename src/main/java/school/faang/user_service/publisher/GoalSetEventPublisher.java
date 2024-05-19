package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.messagebroker.GoalSetEvent;

@Component
@Slf4j
public class GoalSetEventPublisher extends AbstractMessagePublisher<GoalSetEvent> {
    @Value("${spring.data.redis.channels.goal_set_channel.name}")
    private String goalSetTopic;

    public GoalSetEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate, objectMapper);
    }
    @Override
    public void publish(GoalSetEvent goalSetEvent) {
        convertAndSend(goalSetTopic, goalSetEvent);
        log.info("Goal set event published user id: {}", goalSetEvent.getUserId());
    }
}