package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

@Component
public class EventStartEventPublisher implements MessagePublisher<EventDto> {

    private final RedisTemplate<String, EventDto> redisTemplate;
    private final ChannelTopic eventTopic;

    public EventStartEventPublisher(@Qualifier("eventTopic") ChannelTopic eventTopic,
                                    RedisTemplate<String, EventDto> redisTemplate) {
        this.eventTopic = eventTopic;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(EventDto dto) {
        try {
            redisTemplate.convertAndSend(eventTopic.getTopic(), dto);
        } catch (Exception e) {
            throw new RuntimeException("Not able to publish message to topic %s".formatted(eventTopic.getTopic()), e);
        }
    }
}