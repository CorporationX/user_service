package school.faang.user_service.publisher.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.mentorship.MentorshipStartEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipStartPublisher implements MessagePublisher<MentorshipStartEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipStartTopic;

    @Override
    public void publish(MentorshipStartEvent event) {
        redisTemplate.convertAndSend(mentorshipStartTopic.getTopic(), event);
        log.info("Published mentorship start event - {}:{}", mentorshipStartTopic.getTopic(), event);
    }
}