package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEvent;

@Component
public class MentorshipOfferedPublisher extends AbstractEventPublisher<MentorshipOfferedEvent> {

    @Value("${spring.data.redis.channels.mentorship_offered_channel.name}")
    private String mentorshipOfferedChannelName;

    public void publish(MentorshipOfferedEvent mentorshipOfferedEvent) {
        convertAndSend(mentorshipOfferedEvent, mentorshipOfferedChannelName);
    }

}
