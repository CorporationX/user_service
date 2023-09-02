package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.service.redis.RedisMessagePublisher;
import school.faang.user_service.service.redis.events.ProfileViewEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProfileViewEventPublisher {

    private final RedisMessagePublisher redisMessagePublisher;
    private final UserContext userContext;
    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannelName;

    public void publishProfileViewEvent(Long profileViewedId) {
        redisMessagePublisher.publish(profileViewChannelName, new ProfileViewEvent(userContext.getUserId(),
                profileViewedId,
                LocalDateTime.now()));
    }
}
