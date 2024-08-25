package school.faang.user_service.redisPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEventDto;

@Component
public class FollowerEventPublisher extends EventPublisher<FollowerEventDto> {
    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  @Qualifier("followerChannelTopic") ChannelTopic followerChannelTopic,
                                  ObjectMapper objectMapper) {
        super(redisTemplate, followerChannelTopic, objectMapper);
    }
}
