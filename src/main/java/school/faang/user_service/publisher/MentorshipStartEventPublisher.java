package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.MentorshipStartEvent;

@Component
public class MentorshipStartEventPublisher extends EventPublisher<MentorshipStartEvent> {
    public MentorshipStartEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                         ChannelTopic mentorshipChannelTopic,
                                         ObjectMapper objectMapper) {
        super(redisTemplate, mentorshipChannelTopic, objectMapper);
    }
}
