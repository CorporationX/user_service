package school.faang.user_service.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.publishable.MentorshipRequestedEvent;

@Component
public class MentorshipRequestedEventPublisher extends AbstractEventPublisher<MentorshipRequestedEvent> {
    public MentorshipRequestedEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${spring.data.redis.channels.mentorship_requests.name}") String topic) {
        super(redisTemplate, objectMapper, topic);
    }
    public void publish(MentorshipRequestedEvent event) {
        super.publish(event);
    }
}
