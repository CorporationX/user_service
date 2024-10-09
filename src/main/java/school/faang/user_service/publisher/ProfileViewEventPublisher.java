package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.model.dto.ProfileViewEvent;

public class ProfileViewEventPublisher extends RedisEventPublisher<ProfileViewEvent>{
    public ProfileViewEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                     @Qualifier ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}
