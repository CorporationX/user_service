package school.faang.user_service.messaging.publisher.profilepic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.profilepic.ProfilePicEvent;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.messaging.publisher.EventPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfilePicEventPublisher implements EventPublisher<ProfilePicEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic profilePicTopic;

    @Override
    public void publish(ProfilePicEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            log.info("Published ProfilePic event: {}", message);
            redisTemplate.convertAndSend(profilePicTopic.getTopic(), message);
        } catch (JsonProcessingException e) {
            log.error(ExceptionMessages.SERIALIZATION_ERROR + event, e);
        } catch (Exception e) {
            log.error(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
            throw new IllegalArgumentException(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
        }
    }
}
