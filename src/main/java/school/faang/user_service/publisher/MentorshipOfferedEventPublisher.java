package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipOfferedEvent;

@Component
public class MentorshipOfferedEventPublisher extends AbstractEventPublisher<MentorshipOfferedEvent> {
    @Value("${spring.data.redis.channels.mentorship_offered_channel.name}")
    private String MentorshipOfferedChannelName;

    public void publish(MentorshipOfferedEvent event) {
        publish(event, MentorshipOfferedChannelName);
    }
}
