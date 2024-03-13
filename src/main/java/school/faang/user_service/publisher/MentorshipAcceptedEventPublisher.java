package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipAcceptedEventDto;

@Slf4j
@Component
public class MentorshipAcceptedEventPublisher extends AbstractEventPublisher<MentorshipAcceptedEventDto> {

    @Value("${spring.data.redis.channels.mentorship_accepted_channel.name}")
    private String mentorshipAcceptedChannelName;

    public void publish(MentorshipAcceptedEventDto event) {
        log.info("Start send event - " + event);
        super.publish(event, mentorshipAcceptedChannelName);
        log.info("End send event - " + event);
    }
}