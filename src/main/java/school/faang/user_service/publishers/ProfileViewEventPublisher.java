package school.faang.user_service.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.mapper.EventMapper;

@Service
@RequiredArgsConstructor
public class ProfileViewEventPublisher implements MessagePublisher{

    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewTopic;
    private final RedisTemplate redisTemplate;
    private final EventMapper eventMapper;

    @Override
    public <T> void publish(T event) {
        String json = eventMapper.toJson(event);
        redisTemplate.convertAndSend(profileViewTopic, json);
    }
}
