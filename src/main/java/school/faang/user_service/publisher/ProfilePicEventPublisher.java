package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventProfilePic;

@Component
@Slf4j
public class ProfilePicEventPublisher extends AbstractMessagePublisher<EventProfilePic> {

    @Value("${spring.data.redis.channels.profile_pic_channel.name}")
    private String profilePicTopic;

    public ProfilePicEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    public void publish(EventProfilePic event) {
        convertAndSend(profilePicTopic, event);
    }
}
