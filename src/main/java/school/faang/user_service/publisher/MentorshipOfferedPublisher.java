package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

@Component
@RequiredArgsConstructor
public class MentorshipOfferedPublisher extends AbstractEventPublisher<MentorshipRequestDto> {

    private final ChannelTopic mentorshipOfferedTopic;

    public void publish(MentorshipRequestDto mentorshipRequestDto) {
        convertAndSend(mentorshipRequestDto, mentorshipOfferedTopic.getTopic());
    }

}
