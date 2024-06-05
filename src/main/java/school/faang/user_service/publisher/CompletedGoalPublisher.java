package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.GoalCompletedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompletedGoalPublisher implements MessagePublisher<GoalCompletedEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic completedGoalTopic;

    @Override
    public void publish(GoalCompletedEvent event) {
        redisTemplate.convertAndSend(completedGoalTopic.getTopic(), event);
        log.info("Published goal completed event - {}:{}", completedGoalTopic.getTopic(), event);
    }
}
