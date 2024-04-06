package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEvent;

@Component
public class MentorshipOfferedPublisher extends AbstractEventPublisher<MentorshipOfferedEvent> {

    @Value("${spring.data.redis.channels.mentorship_offered_channel.name}")
    private String mentorshipOfferedChannelName;

    public MentorshipOfferedPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(MentorshipOfferedEvent mentorshipOfferedEvent) {
        convertAndSend(mentorshipOfferedEvent, mentorshipOfferedChannelName);
    }

}
