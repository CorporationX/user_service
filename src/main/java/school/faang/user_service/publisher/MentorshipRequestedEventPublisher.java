package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestedEvent;

@Component
public class MentorshipRequestedEventPublisher extends AbstractEventPublisher<MentorshipRequestedEvent> {
    @Value("${spring.data.redis.channels.mentorship_requested_channel.name}")
    private String mentorshipTopicName;

    public void publish(MentorshipRequestedEvent event) {
        super.publish(event, mentorshipTopicName);
    }
}
