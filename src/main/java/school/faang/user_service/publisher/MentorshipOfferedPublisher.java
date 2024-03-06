package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipOfferedPublisher extends AbstractEventPublisher<MentorshipOfferedEvent> {

    private final ChannelTopic mentorshipOfferedTopic;

    public void publish(MentorshipOfferedEvent mentorshipOfferedEvent) {
        convertAndSend(mentorshipOfferedEvent, mentorshipOfferedTopic.getTopic());
    }

}
