package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.ProfilePicEvent;

@Slf4j
@Component
public class ProfilePicEventPublisher extends AbstractEventPublisher {

    @Setter
    @Value("${spring.data.redis.channels.profile_pic_channel.name}")
    private String profilePicEventChannel;

    @Autowired
    public ProfilePicEventPublisher(EventMapper eventMapper, ObjectMapper objectMapper, RedisMessagePublisher redisMessagePublisher) {
        super(eventMapper, objectMapper, redisMessagePublisher);
    }

    public void publish(User user) {
        ProfilePicEvent event = ProfilePicEvent.builder()
                .userId(user.getId())
                .title("Handsome")
                .description("You've uploaded your profile picture!")
                .profilePicLink(user.getUserProfilePic().getSmallFileId())
                .build();

        try {
            String json = objectMapper.writeValueAsString(event);
            redisMessagePublisher.publish(profilePicEventChannel, json);
            log.info("User ID {} successfully uploaded a profile picture", event.getUserId());
        } catch (JsonProcessingException e) {
            log.error("Failed to convert ProfilePicEvent ID {} to JSON", event.getId());
        }
    }
}
