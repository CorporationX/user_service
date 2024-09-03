package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.RedisConfig;
import school.faang.user_service.dto.ProfileViewEvent;
import school.faang.user_service.exception.WriteEntityAsStringException;

@Component
@RequiredArgsConstructor
public class ProfileViewEventPublisher implements RedisConfig.MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    private final ChannelTopic profileViewTopic;

    private final ObjectMapper objectMapper;

    public void publish(ProfileViewEvent profileViewEvent) {
        String jsonProfileViewEvent;
        try {
            jsonProfileViewEvent = objectMapper.writeValueAsString(profileViewEvent);
        } catch (JsonProcessingException e) {
            throw new WriteEntityAsStringException(profileViewEvent, e);
        }
        redisTemplate.convertAndSend(profileViewTopic.getTopic(), jsonProfileViewEvent);
    }
}