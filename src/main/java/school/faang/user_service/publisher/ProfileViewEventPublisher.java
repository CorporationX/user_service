package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.RedisConfig;
import school.faang.user_service.dto.ProfileViewEvent;

@Component
@NoArgsConstructor
public class ProfileViewEventPublisher implements RedisConfig.MessagePublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic userViewTopic;

    @Autowired
    private ObjectMapper objectMapper;

    public void publish(ProfileViewEvent profileViewEvent) {
        String jsonProfileViewEvent;
        try {
            jsonProfileViewEvent = objectMapper.writeValueAsString(profileViewEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(userViewTopic.getTopic(), jsonProfileViewEvent);
    }
}