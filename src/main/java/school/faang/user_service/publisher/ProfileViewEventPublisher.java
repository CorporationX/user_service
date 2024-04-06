package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.ProfileViewEvent;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent> {

    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannelName;
    private final UserContext userContext;

    public ProfileViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     ObjectMapper objectMapper,
                                     UserContext userContext) {
        super(redisTemplate, objectMapper);
        this.userContext = userContext;
    }

    public void publish(long userId) {
        ProfileViewEvent event = ProfileViewEvent.builder()
                .viewedUserId(userId)
                .viewerUserId(userContext.getUserId())
                .receivedAt(LocalDateTime.now())
                .build();

        convertAndSend(event, profileViewChannelName);
    }
}