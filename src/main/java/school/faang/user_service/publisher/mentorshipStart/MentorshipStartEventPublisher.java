package school.faang.user_service.publisher.mentorshipStart;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipStartEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipStartEventPublisher implements MessagePublisher<MentorshipStartEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipStartEventTopic;

    @Override
    public void publish(MentorshipStartEvent message) {
        redisTemplate.convertAndSend(mentorshipStartEventTopic.getTopic(), message);
        log.info("Message was send {}, in topic - {}", message, mentorshipStartEventTopic.getTopic());
    }
}
