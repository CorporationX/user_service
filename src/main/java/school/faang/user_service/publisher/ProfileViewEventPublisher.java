package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent> {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannel;

    @Override
    public void publish(ProfileViewEvent event) {
        try {
            redisTemplate.convertAndSend(profileViewChannel, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Event published id:" + event.getViewingUserId());
    }
}
