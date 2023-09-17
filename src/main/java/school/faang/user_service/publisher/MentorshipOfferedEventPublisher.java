package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEventDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class MentorshipOfferedEventPublisher extends EventPublisher<MentorshipOfferedEventDto> {
    public MentorshipOfferedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                           ObjectMapper objectMapper,
                                           @Value("${spring.data.redis.channels.mentorship_offer_channel.name}") String topicMentorshipOffered) {
        super(redisTemplate, objectMapper, topicMentorshipOffered);
    }

    public void publish(MentorshipOfferedEventDto mentorshipOfferedEventDto) {
        publishToChannel(mentorshipOfferedEventDto);
    }
}
