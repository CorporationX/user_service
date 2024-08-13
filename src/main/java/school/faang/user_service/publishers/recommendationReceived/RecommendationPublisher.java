package school.faang.user_service.publishers.recommendationReceived;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.recommendationReceived.RecommendationReceivedEvent;
import school.faang.user_service.publishers.MessagePublisher;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationPublisher implements MessagePublisher<RecommendationReceivedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationReceivedTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(RecommendationReceivedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(recommendationReceivedTopic.getTopic(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}