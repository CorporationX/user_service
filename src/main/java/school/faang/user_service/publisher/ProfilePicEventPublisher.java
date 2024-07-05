package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.ProfilePicEvent;

@Slf4j
@Service
public class ProfilePicEventPublisher extends AbstractPublisher<ProfilePicEvent> {

    public ProfilePicEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic profilePicTopic) {
        super(redisTemplate);
        channelTopic = profilePicTopic;
    }
}
