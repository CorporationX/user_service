package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfilePicEvent;
import school.faang.user_service.publisher.EventPublisher;

@Component
public class ProfilePicEventPublisher extends EventPublisher<ProfilePicEvent> {
    public ProfilePicEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper objectMapper,
                                    ChannelTopic profilePictureTopic) {
        super(redisTemplate, objectMapper, profilePictureTopic);
    }
}
