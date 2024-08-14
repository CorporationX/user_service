package school.faang.user_service.publisher.mentorship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.mentorship.MentorshipStartEvent;
import school.faang.user_service.publisher.AbstractEventPublisher;

@Component
public class MentorshipStartPublisher extends AbstractEventPublisher<MentorshipStartEvent> {

    @Autowired
    public MentorshipStartPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper objectMapper,
                                    ChannelTopic mentorshipChannelTopic) {
        super(redisTemplate, objectMapper, mentorshipChannelTopic);
    }
}
