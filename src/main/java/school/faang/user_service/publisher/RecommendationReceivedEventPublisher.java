package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import school.faang.user_service.model.EventType;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.RecommendationReceivedEvent;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationReceivedEventPublisher {
    @Setter
    @Value("${spring.data.redis.channels.recommendation_receive_channel.name}")
    private String recommendationReceiveChannelName;

    private final ObjectMapper objectMapper;
    private final RedisMessagePublisher redisMessagePublisher;

    public void sendEvent (Long authorId, Long recipientId, Long recommendationId) {
        RecommendationReceivedEvent recommendationReceivedEvent = new RecommendationReceivedEvent();

        recommendationReceivedEvent.setEventType(EventType.RECOMMENDATION_RECEIVED);
        recommendationReceivedEvent.setAuthorId(authorId);
        recommendationReceivedEvent.setRecipientId(recipientId);
        recommendationReceivedEvent.setRecommendationId(recommendationId);
        recommendationReceivedEvent.setReceivedAt(new Date());

        try {
            String json = objectMapper.writeValueAsString(recommendationReceivedEvent);

            redisMessagePublisher.publish(recommendationReceiveChannelName, json);
            log.info("Recommendation notification was published");
        }  catch (JsonProcessingException e) {
            log.error(e.toString());
        }
    }
}
