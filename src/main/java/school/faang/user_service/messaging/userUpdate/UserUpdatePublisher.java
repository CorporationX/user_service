package school.faang.user_service.messaging.userUpdate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserUpdateEventDto;
import school.faang.user_service.messaging.Publisher;

@Service
@Slf4j
public class UserUpdatePublisher extends Publisher<UserUpdateEventDto> {
    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic userUpdateChannel;
    private ObjectMapper mapper;

    public UserUpdatePublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate, ChannelTopic userUpdateChannel) {
        super(objectMapper);
        this.redisTemplate = redisTemplate;
        this.userUpdateChannel = userUpdateChannel;
    }

    @Override
    public void publish(UserUpdateEventDto user) {
        String eventJson = toJson(user);
        redisTemplate.convertAndSend(userUpdateChannel.getTopic(), eventJson);
    }
}
