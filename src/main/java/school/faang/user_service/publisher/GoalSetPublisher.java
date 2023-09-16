package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.GoalSetEventDto;

@Component
public class GoalSetPublisher extends AbstractPublisher<GoalSetEventDto> {

    public GoalSetPublisher(RedisTemplate<String, Object> redisTemplate,
                            ObjectMapper objectMapper,
                            @Qualifier("goalSetEventChannel") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
