package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.GoalCompletedEvent;

@Service
@Slf4j
public class GoalMessagePublisher extends GenericMessagePublisher<GoalCompletedEvent> {

    public GoalMessagePublisher(RedisTemplate<String, Object> redisTemplate,
                                ChannelTopic goalTopic,
                                ObjectMapper objectMapper) {
        super(redisTemplate, goalTopic, objectMapper);
    }
}
