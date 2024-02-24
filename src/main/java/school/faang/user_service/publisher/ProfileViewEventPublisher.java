package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.ProfileViewEvent;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ProfileViewEventPublisher {
    private final ChannelTopic userViewTopic;
    private final UserContext userContext;
    private final AbstractEventPublisher<ProfileViewEvent> abstractEventPublisher;

    public ProfileViewEventPublisher(ChannelTopic userViewTopic,
                                     RedisTemplate<String, Object> redisTemplate,
                                     ObjectMapper objectMapper,
                                     UserContext userContext) {
        this.userViewTopic = userViewTopic;
        this.userContext = userContext;
        this.abstractEventPublisher = new AbstractEventPublisher<>(
                redisTemplate,
                objectMapper
        );
    }

    public void publish(long userId) {
        ProfileViewEvent event = ProfileViewEvent.builder()
                .viewedUserId(userId)
                .viewerUserId(userContext.getUserId())
                .receivedAt(LocalDateTime.now())
                .build();

        abstractEventPublisher.send(userViewTopic.getTopic(), event);
    }
}