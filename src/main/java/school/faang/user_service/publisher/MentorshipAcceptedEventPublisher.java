package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MessageEvent;
import school.faang.user_service.service.MessagePublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipAcceptedTopic;

    @Async
    @Override
    public void publish(MessageEvent message) {
        log.info("Publishing message on Redis channel {} with content: {}", mentorshipAcceptedTopic.getTopic(), message);
        redisTemplate.convertAndSend(mentorshipAcceptedTopic.getTopic(), message);
    }
}
