package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MessageEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipRequestedChannel;

    @Override
    public void publish(MessageEvent message) {
        log.info("Publishing message on Redis channel {} with content: {}", mentorshipRequestedChannel.getTopic(), message);
        redisTemplate.convertAndSend(mentorshipRequestedChannel.getTopic(), message);
    }
}
