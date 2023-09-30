package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.ProfilePicEventDto;

@Component
public class ProfilePicPublisher extends EventPublisher<ProfilePicEventDto> {

    @Value("${spring.data.redis.channels.profile_pic_channel.name}")
    private String channel;

    public ProfilePicPublisher(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String getChannel() {
        return channel;
    }
}