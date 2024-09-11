package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipAcceptedEvent;

@Component
public class MentorshipAcceptedEventPublisher extends EventPublisher<MentorshipAcceptedEvent> {
    public MentorshipAcceptedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  @Qualifier("mentorshipAcceptedChannelTopic") ChannelTopic mentorshipAcceptedChannelTopic,
                                  ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, mentorshipAcceptedChannelTopic);
    }
}
