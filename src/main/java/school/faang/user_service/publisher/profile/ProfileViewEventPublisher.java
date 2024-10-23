package school.faang.user_service.publisher.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.profile.NewProfileViewEventDto;
import school.faang.user_service.publisher.MessagePublisher;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProfileViewEventPublisher implements MessagePublisher<NewProfileViewEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic profileViewChannel;

    @Override
    public void publish(NewProfileViewEventDto profileView) {
        redisTemplate.convertAndSend(profileViewChannel.getTopic(), profileView);
        log.debug("Profile view event sent {}, in topic - {}", profileView, profileViewChannel.getTopic());
    }
}
