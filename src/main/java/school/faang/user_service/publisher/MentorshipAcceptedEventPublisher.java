package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipAcceptedEvent;
@Component
@Slf4j
public class MentorshipAcceptedEventPublisher extends AbstractEventPublisher<MentorshipAcceptedEvent> {
    @Value("${spring.data.redis.channels.mentorship_accepted_channel.name}")
    private ChannelTopic channelTopic;

    public MentorshipAcceptedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }
    public void publish(MentorshipAcceptedEvent event) {
        convertAndSend(event,channelTopic.getTopic());
        log.info("Mentorship request event published request id: " + event.getRequestId());
    }
}
