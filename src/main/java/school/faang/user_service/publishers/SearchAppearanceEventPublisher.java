package school.faang.user_service.publishers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchAppearanceEventPublisher implements MessagePublisher {

    @Value("${spring.data.redis.channels.profile_search_topic.name}")
    private String profileSearchTopic;
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(profileSearchTopic, message);
    }
}
