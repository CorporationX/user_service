package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.EventType;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.PremiumEvent;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumEventPublisher {
    @Setter
    @Value("${spring.data.redis.channels.premium_events_channel.name}")
    private String premiumEventChannelName;

    private final ObjectMapper objectMapper;
    private final RedisMessagePublisher redisMessagePublisher;

    public void purchaseSuccessful(Long userId) {
        PremiumEvent premiumEvent = new PremiumEvent();

        premiumEvent.setEventType(EventType.PREMIUM_PURCHASED);
        premiumEvent.setUserId(userId);
        premiumEvent.setReceivedAt(new Date());

        try {
            String json = objectMapper.writeValueAsString(premiumEvent);

            redisMessagePublisher.publish(premiumEventChannelName, json);
            log.info("Premium purchase success notification was published");
        }  catch (JsonProcessingException e) {
            log.error(e.toString());
        }
    }
}
