package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.message.SkillAcquiredEventMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillAcquiredEventPublisher implements MessagePublisher<SkillAcquiredEventMessage> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipRequestTopic;

    @Override
    public void publish(SkillAcquiredEventMessage skillAcquiredEventMessage) {
        log.info("message publish: {}", skillAcquiredEventMessage.toString());
        redisTemplate.convertAndSend(mentorshipRequestTopic.getTopic(), skillAcquiredEventMessage);
    }
}
