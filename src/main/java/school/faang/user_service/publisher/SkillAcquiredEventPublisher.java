package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MessageEvent;
import school.faang.user_service.service.MessagePublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillAcquiredEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic skillTopic;

    @Override
    public void publish(MessageEvent message) {
        log.info("Publishing message on Redis channel {} with content: {}", skillTopic.getTopic(), message);
        redisTemplate.convertAndSend(skillTopic.getTopic(), message);
    }
}
