package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.message.MentorshipRequestMessage;

@Component
@AllArgsConstructor
@Slf4j
@ToString
public class MentorshipRequestEventPublisher implements MessagePublisher<MentorshipRequestMessage> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipRequestTopic;

    @Override
    public void publish(MentorshipRequestMessage mentorshipRequestMessage) {
        log.info("message publish: {}", mentorshipRequestMessage.toString());
        redisTemplate.convertAndSend(mentorshipRequestTopic.getTopic(), mentorshipRequestMessage);
    }
}
