package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.ProfileViewEventDto;

@Service
public class ProfileViewEventPublisher extends AbstractPublisher<ProfileViewEventDto> {
    public ProfileViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                     ObjectMapper jsonMapper,
                                     @Value("${spring.data.redis.channels.profile_view_channel.name}")  String profileViewTopic) {
        super(redisTemplate, jsonMapper, profileViewTopic);
    }
}
