package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipStartEvent;

@Component
public class MentorshipEventPublisher extends AbstractEventPublisher<MentorshipStartEvent> {
    @Value("${spring.data.redis.channels.mentorship_channel.name}")
    private String mentorshipChannelName;

    public void publish(MentorshipStartEvent event) {
        super.publish(event, mentorshipChannelName);
    }
}
