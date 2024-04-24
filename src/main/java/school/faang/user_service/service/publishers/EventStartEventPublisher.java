package school.faang.user_service.service.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventStartEvent;

@Service
@RequiredArgsConstructor

public class EventStartEventPublisher implements MessagePublisher<EventStartEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value( "${spring.data.redis.channel.event_start_channel}" )
    private final String eventStartChannel;


    @Override
    public void publish(EventStartEvent message) {
        redisTemplate.convertAndSend( eventStartChannel, message );

    }
}
