package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestedEventDto;

@Component
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher extends EventPublisher<MentorshipRequestedEventDto> {

    private final ChannelTopic mentorshipRequestTopic;

    public void publish(MentorshipRequestedEventDto eventDto) {
        convertAndSend(eventDto, mentorshipRequestTopic.getTopic());
    }
}
