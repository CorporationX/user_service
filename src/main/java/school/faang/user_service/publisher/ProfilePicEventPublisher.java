package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.userprofile.ProfilePicEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfilePicEventPublisher{

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    @Qualifier("profilePicChannel") private final ChannelTopic channel;

    public void publish(@NonNull ProfilePicEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            stringRedisTemplate.convertAndSend(channel.getTopic(), json);
            log.info("Published ProfilePicEvent: {} to \"{}\"", json, channel.getTopic());
        } catch (JsonProcessingException exception) {
            log.error("Failed to serialize ProfilePicEvent for user {}: {}", event.getUserId(), exception.getMessage());
            throw new SerializationFailedException("Event serialization failed", exception);
        }
    }
}
