package school.faang.user_service.publishers;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Setter
@Slf4j
public class MentorshipOfferedPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipOfferedChannel;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(mentorshipOfferedChannel.getTopic(), message);
    }
}
