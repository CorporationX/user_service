package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.ProfileViewEvent;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent>{
    private final ChannelTopic userViewTopic;

    public void publish(ProfileViewEvent profileViewEvent) {
        log.info("Event was published {}", profileViewEvent);
        convertAndSend(profileViewEvent, userViewTopic.getTopic());
    }

    public void publish(long userId, Long ownerId) {
        publish(ProfileViewEvent.builder()
                .userId(userId)
                .ownerId(ownerId)
                .receivedAt(LocalDateTime.now())
                .build());
    }
}
