package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.ProfileViewEventDto;

@Component
@RequiredArgsConstructor
public class ProfileViewPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannel;
    public void publish(ProfileViewEventDto event) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(event);
        redisTemplate.convertAndSend(profileViewChannel, json);
    }
}
