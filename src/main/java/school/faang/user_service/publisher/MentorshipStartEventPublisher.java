package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipStartEvent;

@Component
public class MentorshipStartEventPublisher extends AbstractEventPublisher<MentorshipStartEvent> {

    private final ChannelTopic mentorshipTopic;

    public MentorshipStartEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                         ObjectMapper objectMapper,
                                         @Qualifier("mentorshipChannel") ChannelTopic mentorshipTopic) {

        super(redisTemplate, objectMapper);
        this.mentorshipTopic = mentorshipTopic;
    }

    public void sendEvent(MentorshipStartEvent event) {
        publish(mentorshipTopic, event);
    }
}
