package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfilePicEventDto;

@Component
public class ProfilePicEventPublisher extends AbstractEventPublisher<ProfilePicEventDto> {

    public ProfilePicEventPublisher(RedisTemplate<String, ProfilePicEventDto> redisTemplate,
                                    @Qualifier("profilePicTopic") ChannelTopic channelTopic) {
        super(redisTemplate, channelTopic);
    }
}