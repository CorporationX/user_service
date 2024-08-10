package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.GoalCompletedEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalMessagePublisher implements MessagePublisher<GoalCompletedEvent>{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(GoalCompletedEvent goalCompletedEvent) {
        String message;
        try {
            message = objectMapper.writeValueAsString(goalCompletedEvent);
        } catch (JsonProcessingException e) {
            log.info("Method: publish {}", e.getMessage());
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(topic.getTopic(), message);
        System.out.println("Published message: " + message + " to channel: " + topic.getTopic());
    }
}
