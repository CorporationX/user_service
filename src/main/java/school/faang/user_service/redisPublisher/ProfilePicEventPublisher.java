package school.faang.user_service.redisPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfilePicEvent;

@Component
public class ProfilePicEventPublisher extends EventPublisher<ProfilePicEvent> {
    public ProfilePicEventPublisher(RedisTemplate redisTemplate, ChannelTopic profilePictureTopic, ObjectMapper objectMapper) {
        super(redisTemplate, profilePictureTopic, objectMapper);
    }
}
