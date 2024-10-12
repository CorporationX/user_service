package school.faang.user_service.config.redis.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.ProfileViewEventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisProfileViewEventPublisher implements ProfileViewEventPublisher {
    private final RedisTemplate<String, ProfileViewEventDto> profileViewEventDtoRedisTemplate;
    private final ChannelTopic profileViewEventTopic;

    @Override
    public void publish(ProfileViewEventDto profileViewEventDto) {
        profileViewEventDtoRedisTemplate.convertAndSend(profileViewEventTopic.getTopic(), profileViewEventDto);
        log.info("Publish into topic: {}, message: {}", profileViewEventTopic.getTopic(), profileViewEventDto);
    }
}
