package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.Event;
import school.faang.user_service.event.ProfilePicEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfilePicEventPublisher implements MessagePublisher<ProfilePicEvent> {

    private final RedisTemplate<String, Event> redisTemplate;

    @Value("${spring.data.redis.channels.profile_pic_channel}")
    private String topic;

    @Override
    public void publish(ProfilePicEvent event) {
        log.info("Sending to Redis: {}", event);
        redisTemplate.convertAndSend(topic, event);
    }
}
