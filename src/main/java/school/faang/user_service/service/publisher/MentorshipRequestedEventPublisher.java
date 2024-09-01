package school.faang.user_service.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.publishable.MentorshipRequestEvent;

@Component
public class MentorshipRequestedEventPublisher extends AbstractEventPublisher<MentorshipRequestEvent> {
    public MentorshipRequestedEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${spring.data.redis.channels.mentorship_requests.name}") String topic) {
        super(redisTemplate, objectMapper, topic);
    }
    public void publish(MentorshipRequestEvent event) {
        super.publish(event);
    }
}
