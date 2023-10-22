package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.messaging.events.ProfileViewEvent;

@Service
@RequiredArgsConstructor
public class ProfileViewEventPublisher implements MessagePublisher<ProfileViewEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final @Qualifier("viewProfileTopic") ChannelTopic topic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(ProfileViewEvent event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }
}
