package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, ChannelTopic> topicMap;

    public void publishEvent(String topicName, Object message) {
        ChannelTopic topic = topicMap.get(topicName);
        if (topic != null) {
            try {
                redisTemplate.convertAndSend(topic.getTopic(), message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("Topic not found: " + topicName);
        }
    }
}


