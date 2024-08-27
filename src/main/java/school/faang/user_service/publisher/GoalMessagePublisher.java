package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.GoalCompletedEvent;

@Service
public class GoalMessagePublisher extends GenericMessagePublisher<GoalCompletedEvent> {
    private final ChannelTopic goalTopic;

    public GoalMessagePublisher(RedisTemplate<String, Object> redisTemplate,
                                ChannelTopic goalTopic,
                                ObjectMapper objectMapper) {
        super(redisTemplate,  objectMapper);
        this.goalTopic = goalTopic;
    }

    @Override
    public ChannelTopic getTopic() {
        return goalTopic;
    }
}
