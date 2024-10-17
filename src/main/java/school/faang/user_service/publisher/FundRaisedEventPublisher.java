package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.FundRaisedEvent;

@Component
@RequiredArgsConstructor
public class FundRaisedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.fund-raised-channel.name}")
    private String fundRaisedTopic;

    public void publish(FundRaisedEvent fundRaisedEvent) {
        redisTemplate.convertAndSend(fundRaisedTopic, fundRaisedEvent);
    }
}
