package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEvent;

@Component
@Slf4j
public class ProfileViewEventPublisher extends AbstractMessagePublisher<ProfileViewEvent> {
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannel;

    public ProfileViewEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate, objectMapper);
    }

    public void publish(ProfileViewEvent event) {
        convertAndSend(profileViewChannel, event);
    }
}
