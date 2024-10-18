package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<String, ChannelTopic> channelTopics;

    public void publishToTopic(String topicName, Object message) {
        ChannelTopic topic = channelTopics.get(topicName);
        if (topic != null) {
            redisTemplate.convertAndSend(topic.getTopic(), message);
        } else {
            throw new IllegalArgumentException("Unknown topic: " + topicName);
        }
    }
}
