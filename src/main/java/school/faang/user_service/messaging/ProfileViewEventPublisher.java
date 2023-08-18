package school.faang.user_service.messaging;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.messaging.events.ProfileViewEvent;

@Component
public class ProfileViewEventPublisher implements MessagePublisher<ProfileViewEvent> {

    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic topic;

    public ProfileViewEventPublisher() {
    }

    public ProfileViewEventPublisher(
            RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(ProfileViewEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
