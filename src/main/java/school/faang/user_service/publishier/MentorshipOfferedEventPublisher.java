package school.faang.user_service.publishier;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.MentorshipOfferedEvent;

@Component
public class MentorshipOfferedEventPublisher extends EventPublisher<MentorshipOfferedEvent> {

    public MentorshipOfferedEventPublisher(ObjectMapper objectMapper,
                                           RedisTemplate<String, Object> redisTemplate,
                                           ChannelTopic mentorshipOfferedChannel) {
        super(objectMapper, redisTemplate, mentorshipOfferedChannel);
    }
}