package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventProfilePic;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfilePicEventPublisher extends AbstractEventPublisher<EventProfilePic>{
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channels.profile_pic_channel.name}")
    private String profilePicTopic;

    @Override
    public void publish(EventProfilePic event) {
        try {
            redisTemplate.convertAndSend(profilePicTopic, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Event published: " + event);
    }
}
