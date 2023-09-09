package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.model.EventType;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.PremiumEvent;

import java.util.Date;

@Slf4j
@Component
public class PremiumEventPublisher extends AbstractEventPublisher {
    @Setter
    @Value("${spring.data.redis.channels.premium_events_channel.name}")
    private String premiumEventChannelName;

    @Autowired
    public PremiumEventPublisher(EventMapper eventMapper, ObjectMapper objectMapper,
                                 RedisMessagePublisher redisMessagePublisher) {
        super(eventMapper, objectMapper, redisMessagePublisher);
    }

    public void purchaseSuccessful(Long userId) {
        PremiumEvent premiumEvent = new PremiumEvent();

        premiumEvent.setEventType(EventType.PREMIUM_PURCHASED);
        premiumEvent.setUserId(userId);
        premiumEvent.setReceivedAt(new Date());

        redisMessagePublisher.publish(premiumEventChannelName, premiumEvent);
    }
}
