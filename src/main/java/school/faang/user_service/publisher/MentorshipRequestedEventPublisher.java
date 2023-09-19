package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipAcceptedEventDto;

@Component
public class MentorshipRequestedEventPublisher extends AbstractEventPublisher<MentorshipAcceptedEventDto>{

    public MentorshipRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                             @Value("${spring.data.redis.channels.mentorship_accepted_request_event_channel}") String topicMentorshipAcceptedRequestEvent) {
        super(redisTemplate, objectMapper, topicMentorshipAcceptedRequestEvent);
    }

    public void publish(MentorshipAcceptedEventDto event){
        publishInTopic(event);
    }
}
