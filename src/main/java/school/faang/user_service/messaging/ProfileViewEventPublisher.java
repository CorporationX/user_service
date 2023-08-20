package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.messaging.events.ProfileViewEvent;

@Service
@RequiredArgsConstructor
public class ProfileViewEventPublisher implements MessagePublisher<ProfileViewEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(ProfileViewEvent event){
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
