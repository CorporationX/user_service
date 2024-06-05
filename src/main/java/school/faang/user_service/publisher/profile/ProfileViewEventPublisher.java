package school.faang.user_service.publisher.profile;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.profile.ProfileViewEvent;
import school.faang.user_service.publisher.AbstractEventPublisher;

@Slf4j
@Component
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent> {

    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewEventChannel;

    public ProfileViewEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    @Override
    public void publish(ProfileViewEvent event) {
        convertAndSend(event, profileViewEventChannel);
    }
}
