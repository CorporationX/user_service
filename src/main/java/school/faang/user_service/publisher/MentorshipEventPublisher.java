package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.Event;
import school.faang.user_service.event.MentorshipEvent;

@RequiredArgsConstructor
@Component
public class MentorshipEventPublisher implements MessagePublisher<MentorshipEvent> {

    @Value("${spring.data.redis.channels.mentorship_offered}")
    private String channel;

    private final RedisTemplate<String, Event> redisTemplate;

    @Override
    public void publish(MentorshipEvent event) {
        redisTemplate.convertAndSend(channel, event);
    }
}
