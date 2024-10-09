package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventStartEventPublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    public void publishEventStart(Long eventId, List<Long> participantIds) {
        if (topic == null) {
            throw new NullPointerException("Topic is null. Event not published.");
        }
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate is not initialized");
        }
        EventStartEvent event = new EventStartEvent(eventId, participantIds);
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
