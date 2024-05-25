package school.faang.user_service.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartEvent;

@Component

public class EventStartEventPublisher extends AbstractPublisher<EventStartEvent> {





    public EventStartEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper jsonMapper,  @Value( "${spring.data.redis.channel.event_start_channel}" )String eventStartChannel) {

        super(redisTemplate, jsonMapper, eventStartChannel);

    }


}
