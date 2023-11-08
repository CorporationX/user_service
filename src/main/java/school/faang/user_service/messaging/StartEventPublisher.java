package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

@Component
@RequiredArgsConstructor
public class StartEventPublisher implements MessagePublisher<EventDto>{
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channels.start_event_chanel.name}")
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(EventDto eventDto) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(eventDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }
}
