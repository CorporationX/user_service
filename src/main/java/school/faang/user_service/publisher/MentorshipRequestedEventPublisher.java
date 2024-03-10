package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipRequestedEvent;

@Component
public class MentorshipRequestedEventPublisher extends AbstractEventPublisher<MentorshipRequestedEvent>{
    @Value("${spring.data.redis.channels.mentorship_requested_channel.name}")
    private String mentorshipRequestedChannelName;

    public MentorshipRequestedEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    public void publish(MentorshipRequestedEvent mentorshipRequestedEvent) {
        convertAndSend(mentorshipRequestedEvent, mentorshipRequestedChannelName);
    }
}
