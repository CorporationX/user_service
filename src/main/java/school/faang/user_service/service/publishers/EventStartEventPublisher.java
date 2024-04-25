package school.faang.user_service.service.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartEvent;

@Component
@RequiredArgsConstructor

public class EventStartEventPublisher implements MessagePublisher<EventStartEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value( "${spring.data.redis.channel.event-start-channel}" )
    private final String eventStartChannel;


    @Override
    public void publish(EventStartEvent message) {
        redisTemplate.convertAndSend( eventStartChannel, message );

    }
}
