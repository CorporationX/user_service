package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.MentorshipRequestEvent;

@Component
public class MentorshipRequestEventPublisher extends EventPublisher<MentorshipRequestEvent> {


    public MentorshipRequestEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                           ObjectMapper objectMapper,
                                           @Qualifier("mentorshipRequestTopic") ChannelTopic mentorshipRequestTopic) {
        super(redisTemplate, objectMapper, mentorshipRequestTopic);
    }
}
