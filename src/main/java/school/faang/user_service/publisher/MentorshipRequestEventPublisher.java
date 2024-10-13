package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.message.MentorshipRequestMessage;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class MentorshipRequestEventPublisher {

    private RedisTemplate<String, MentorshipRequestDto> redisTemplate;
    private ChannelTopic topic;

    public void publish(MentorshipRequestMessage mentorshipRequestMessage) {
        redisTemplate.convertAndSend(topic.getTopic(), mentorshipRequestMessage);
    }
}
