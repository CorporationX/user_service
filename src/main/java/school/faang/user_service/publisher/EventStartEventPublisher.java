package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.EventStartEvent;

import java.util.List;

@Service
public class EventStartEventPublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChannelTopic topic;

    public void publishEventStart(Long eventId, List<Long> participantIds) {
        if (eventId == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
        if (participantIds == null || participantIds.isEmpty()) {
            throw new IllegalArgumentException("Participant IDs cannot be null or empty");
        }
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
