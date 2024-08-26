package school.faang.user_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStartEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic eventStartEventTopic;

    public void publish(EventStartEvent eventStartEvent) {
        log.info("Publication event start event with event id: {}", eventStartEvent.getEventId());
        redisTemplate.convertAndSend(eventStartEventTopic.getTopic(), eventStartEvent);
    }
}