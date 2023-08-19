package school.faang.user_service.messaging;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.messaging.events.ProfileViewEvent;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class ProfileViewEventPublisher implements MessagePublisher<ProfileViewEvent> {

    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic topic;

    @Override
    public void publish(ProfileViewEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
