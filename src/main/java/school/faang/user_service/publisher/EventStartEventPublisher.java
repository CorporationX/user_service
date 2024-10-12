package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

@Component
@RequiredArgsConstructor
public class EventStartEventPublisher implements MessagePublisher<EventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.event-event-channel.name}")
    private String topic;

    @Override
    public void publish(EventDto dto) {
        try {
            String message = objectMapper.writeValueAsString(dto);
            redisTemplate.convertAndSend(topic, message);
        } catch (Exception e) {
            throw new RuntimeException("Not able to publish message to topic %s".formatted(topic), e);
        }
    }
}