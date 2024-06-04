package school.faang.user_service.service;

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
    private final ChannelTopic topic;

    @Override
    public void publish(MessageEvent message) {
        log.info("Publishing message on Redis channel {} with content: {}", topic.getTopic(), message);
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
