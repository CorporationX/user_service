package school.faang.user_service.config.redis.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.ProfileViewEventDto;

@Slf4j
@Component
public class RedisProfileViewEventPublisher implements ProfileViewEventPublisher {
    private final RedisTemplate<String, ProfileViewEventDto> redisTemplate;
    private final ChannelTopic channelTopic;

    public RedisProfileViewEventPublisher(RedisTemplate<String, ProfileViewEventDto> redisTemplate,
                                          @Qualifier("profileViewEventTopic")
                                          ChannelTopic channelTopic) {
        this.redisTemplate = redisTemplate;
        this.channelTopic = channelTopic;
    }

    @Override
    public void publish(ProfileViewEventDto profileViewEventDto) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), profileViewEventDto);
        log.info("Publish into topic: {}, message: {}", channelTopic.getTopic(), profileViewEventDto);
    }
}
