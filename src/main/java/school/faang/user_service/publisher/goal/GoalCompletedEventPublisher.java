package school.faang.user_service.publisher.goal;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.publisher.AbstractEventPublisher;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Component
public class GoalCompletedEventPublisher extends AbstractEventPublisher implements MessagePublisher {

    public GoalCompletedEventPublisher(
            ObjectMapper objectMapper,
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("achievementGoalTopic") ChannelTopic achievementGoalTopic
    ) {
        super(objectMapper, redisTemplate, achievementGoalTopic);
    }

    @Override
    public void publish(Object object) {
        convertAndSend(object);
    }
}
