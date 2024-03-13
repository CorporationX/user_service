package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.events.MentorshipRequestedEventDto;

@Component
public class MentorshipRequestedEventPublisher extends EventPublisher<MentorshipRequestedEventDto> {

    @Value("${spring.data.redis.channels.mentorship_request_channel.name}")
    private String mentorshipRequestChannelName;

    public MentorshipRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(MentorshipRequestedEventDto eventDto) {
        convertAndSend(eventDto, mentorshipRequestChannelName);
    }
}
