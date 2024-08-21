package school.faang.user_service.publisher.mentorship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.mentorship.MentorshipRequestEvent;
import school.faang.user_service.publisher.AbstractEventPublisher;

@Component
public class MentorshipRequestPublisher extends AbstractEventPublisher<MentorshipRequestEvent> {
    @Autowired
    public MentorshipRequestPublisher(RedisTemplate<String, Object> redisTemplate,
                                      ObjectMapper objectMapper,
                                      ChannelTopic mentroshipRequestChannelTopic) {
        super(redisTemplate, objectMapper, mentroshipRequestChannelTopic);
    }
}
