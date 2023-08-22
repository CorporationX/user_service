package school.faang.user_service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEventDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipOfferedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.topic.mentorshipOffered}")
    private String topicMentorshipOffered;

    public void publish(MentorshipOfferedEventDto mentorshipOfferedEventDto) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(mentorshipOfferedEventDto);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        redisTemplate.convertAndSend(topicMentorshipOffered, json);
    }
}
