package school.faang.user_service.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.ProfileViewEventDTO;

@RequiredArgsConstructor
@Service
public class ProfileViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic profileViewTopic; // Изменено на profileViewTopic
    private final ObjectMapper objectMapper;

    public void publishProfileViewEvent(ProfileViewEventDTO profileViewEventDTO) {
        redisTemplate.convertAndSend(profileViewTopic.getTopic(), profileViewEventDTO);
    }
}