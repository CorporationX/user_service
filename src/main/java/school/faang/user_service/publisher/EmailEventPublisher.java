package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.email.EmailEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.channels.email.name}")
    private String emailTopic;

    public void publish(EmailEvent emailEvent) {
        try {
            String json = objectMapper.writeValueAsString(emailEvent);
            redisTemplate.convertAndSend(emailTopic, json);
            log.info("String json create");
        } catch (JsonProcessingException e) {
            log.error("String json not create {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
