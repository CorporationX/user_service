package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipAcceptedEventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher implements MessagePublisher<MentorshipAcceptedEventDto> {
    private final RedisTemplate<String, MentorshipAcceptedEventDto> redisTemplate;

    @Qualifier("mentorshipEventChannel")
    private final ChannelTopic mentorshipEventTopic;

    @Override
    @Retryable(retryFor = RuntimeException.class,
            backoff = @Backoff(delayExpression = "${spring.data.redis.publisher.delay}"))
    public void publish(MentorshipAcceptedEventDto event) {
        try {
            redisTemplate.convertAndSend(mentorshipEventTopic.getTopic(), event);
            log.debug("Published mentorship accepted event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish mentorship accepted event: {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
