package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipStartEvent;

@Component
public class MentorshipStartEventPublisher extends AbstractEventPublisher<MentorshipStartEvent> {
    public MentorshipStartEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Qualifier("mentorshipTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
