package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.message.MentorshipRequestMessage;

@Component
@AllArgsConstructor
@Slf4j
public class MentorshipRequestEventPublisher implements MessagePublisher {

    private RedisTemplate<String, MentorshipRequestDto> redisTemplate;
    private ChannelTopic mentorshipRequestTopic;

    @Override
    public void publish(MentorshipRequestMessage mentorshipRequestMessage) {
        log.info("message send: {}", mentorshipRequestMessage);
        redisTemplate.convertAndSend(mentorshipRequestTopic.getTopic(), mentorshipRequestMessage);
    }
}
