package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestEvent;

import java.time.LocalDateTime;

@Component
public class MentorshipRequestedEventPublisher extends AbstractEventPublisher<MentorshipRequestEvent>{

    public MentorshipRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                             @Value("${spring.data.redis.channels.mentorship_request_channel}")
                                             String channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }

    public void publish(MentorshipRequestEvent mentorshipRequestEvent) {
        mentorshipRequestEvent.setLocalDateTime(LocalDateTime.now());
        publishInTopic(mentorshipRequestEvent);
    }
}
