package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipChannel;

    public void publish(MentorshipRequestedEvent event) {
        redisTemplate.convertAndSend(mentorshipChannel.getTopic(), event);
        log.info("Message published: {}", event);
    }
}
