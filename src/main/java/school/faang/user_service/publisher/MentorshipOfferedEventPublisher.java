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
@RequiredArgsConstructor
@Slf4j
public class MentorshipOfferedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.channels.mentorship_offer_channel.name}")
    private String topicMentorshipOffered;

    public void publish(MentorshipOfferedEventDto mentorshipOfferedEventDto) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(mentorshipOfferedEventDto);
        } catch (JsonProcessingException e) {
            log.error("Can't use publish ",e.getMessage());
            throw new DataValidationException("Problem with json");
        }
        redisTemplate.convertAndSend(topicMentorshipOffered, json);
    }
}
