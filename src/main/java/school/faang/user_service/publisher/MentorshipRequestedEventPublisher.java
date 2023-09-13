package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestEvent;

@Component
public class MentorshipRequestedEventPublisher extends AbstractEventPublisher<MentorshipRequestEvent>{
    private final ChannelTopic topicMentorshipRequest;

    public MentorshipRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                             ChannelTopic topicMentorshipRequest) {
        super(redisTemplate, objectMapper);
        this.topicMentorshipRequest = topicMentorshipRequest;
    }

    public void publish(MentorshipRequestEvent mentorshipRequestEvent) {
        publishInTopic(topicMentorshipRequest, mentorshipRequestEvent);
    }
}
