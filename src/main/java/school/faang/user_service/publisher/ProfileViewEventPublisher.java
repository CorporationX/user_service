package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.ProfileViewEvent;

@Component
public class ProfileViewEventPublisher extends RedisEventPublisher<ProfileViewEvent> {
    public ProfileViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                         ObjectMapper objectMapper,
                                         @Qualifier("profileViewTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
