package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.EventRegistrationNotificationDto;

@Service
@Slf4j
@AllArgsConstructor
public class EventRegistrationEventPublisher implements MessagePublisher {
    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic topicEventParticipation;

    @Override
    public void publish(EventRegistrationNotificationDto message) {
        redisTemplate.convertAndSend(topicEventParticipation.getTopic(), message);
        log.info("Message published: {}", message);
    }
}
