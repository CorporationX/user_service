package school.faang.user_service.messaging.userUpdate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserUpdateEventDto;
import school.faang.user_service.util.Mapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserUpdatePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userUpdateChannel;
    private final Mapper mapper;

    public void publish(UserUpdateEventDto user) {
        String eventJson = mapper.toJson(user);
        redisTemplate.convertAndSend(userUpdateChannel.getTopic(), eventJson);
    }
}
