package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEvent;

@Slf4j
@Component
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent> {

    public ProfileViewEventPublisher(RedisTemplate<String, ProfileViewEvent> redisTemplate,
                                     @Qualifier("profileViewChannel")
                                     ChannelTopic channelTopic) {
        super(redisTemplate, channelTopic);
    }
}
