package school.faang.user_service.publisher.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.mapper.JsonMapper;

@Component
@RequiredArgsConstructor
public class EventStartPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonMapper<EventStartDto> jsonMapper;
    @Value("${spring.data.redis.channels.event_channel.name}")
    private String channel;

    public void publish(EventStartDto eventStartDto) {
        String json = jsonMapper.toJson(eventStartDto);
        redisTemplate.convertAndSend(channel, json);
    }
}
