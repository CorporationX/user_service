package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipStartEventDto;

@Component
public class MentorshipEventPublisher extends AbstractEventPublisher<MentorshipStartEventDto> {

    @Value("${spring.data.redis.channels.mentorship_channel.name}")
    private String mentorshipChannel;

    public MentorshipEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    @Override
    public void publish(MentorshipStartEventDto event) {
        convertAndSend(event, mentorshipChannel);
    }

}
