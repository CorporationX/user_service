package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.redis_event.GoalCompletedRedisEvent;

@Component
public class GoalCompletedRedisEventPublisher extends RedisEventPublisher<GoalCompletedRedisEvent> {
    public GoalCompletedRedisEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                            ObjectMapper objectMapper,
                                            @Qualifier("goalCompletedTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
