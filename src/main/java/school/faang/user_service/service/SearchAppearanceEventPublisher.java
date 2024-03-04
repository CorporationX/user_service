package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

/**
 * @author Alexander Bulgakov
 */
@AllArgsConstructor
@NoArgsConstructor
public class SearchAppearanceEventPublisher implements MessagePublisher {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic topic;

    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
