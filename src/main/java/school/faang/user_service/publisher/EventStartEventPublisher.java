package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.event.EventStartEvent;

import java.util.List;

@Service
public class EventStartEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Autowired
    public EventStartEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publishEventStart(Long eventId, List<Long> participantIds) {
        EventStartEvent event = new EventStartEvent(eventId, participantIds);
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}