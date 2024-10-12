package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.GoalCompletedEventDto;

@RequiredArgsConstructor
@Component
public class GoalCompletedEventPublisher implements MessagePublisher<GoalCompletedEventDto> {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.goal-event-channel.name}")
    private String topic;

    @Override
    public void publish(GoalCompletedEventDto goalCompletedEventDto) {
        try {
            String message = objectMapper.writeValueAsString(goalCompletedEventDto);
            redisTemplate.convertAndSend(topic, message);
        } catch (Exception e) {
            throw new RuntimeException("Not able to publish message to topic %s".formatted(topic), e);
        }
    }
}