package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEventDto;

@Component
public class MentorshipOfferedEventPublisher extends AbstractEventPublisher<MentorshipOfferedEventDto> {
    public MentorshipOfferedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                           ObjectMapper objectMapper,
                                           @Value("${spring.data.redis.channels.mentorship_offer_channel}") String topicMentorshipOffered) {
        super(redisTemplate, objectMapper, topicMentorshipOffered);
    }

    public void publish(MentorshipOfferedEventDto mentorshipOfferedEventDto) {
        publishInTopic(mentorshipOfferedEventDto);
    }
}
