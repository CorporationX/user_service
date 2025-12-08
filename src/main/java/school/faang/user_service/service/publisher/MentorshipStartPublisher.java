package school.faang.user_service.service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipStartEvent;

@RequiredArgsConstructor
@Component
public class MentorshipStartPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipChannel;

    public void publish(MentorshipStartEvent event) {
        redisTemplate.convertAndSend(mentorshipChannel.getTopic(), event);
    }
}