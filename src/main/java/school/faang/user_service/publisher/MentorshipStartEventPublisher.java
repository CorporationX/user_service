package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.DtoMentorshipStartEvent;

@Component
public class MentorshipStartEventPublisher extends EventPublisher<DtoMentorshipStartEvent> {
    @Value("${spring.data.redis.channels.mentorship_channel}")
    private String channel;

    public MentorshipStartEventPublisher(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String getChannel() {
        return channel;
    }
}
