package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.publisher.MentorshipEventDto;
import school.faang.user_service.exception.EventSerializationException;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipEventPublisher {

    @Value("${spring.data.redis.channel.mentorship}")
    private String mentorshipTopic;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(MentorshipEventDto event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(mentorshipTopic, json);
            log.info("Published Mentorship Event: {} to '{}'", json, mentorshipTopic);
        } catch (JsonProcessingException e) {
            log.error("Error serializing Mentorship Event: {}", event, e);
            throw new EventSerializationException("Error serializing Mentorship Event", e);
        }
    }
}
