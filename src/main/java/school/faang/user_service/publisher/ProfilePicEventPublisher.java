package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventProfilePic;

@Component
@RequiredArgsConstructor
public class ProfilePicEventPublisher extends AbstractEventPublisher<EventProfilePic>{
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channels.profile_pic_channel.name}")
    private String profilePicTopic;

    @Override
    public void publish(EventProfilePic event) {
        redisTemplate.convertAndSend(profilePicTopic, event);
    }
}
