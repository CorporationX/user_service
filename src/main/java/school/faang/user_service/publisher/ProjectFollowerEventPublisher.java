package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.ProjectFollowerEventDto;

@Component
public class ProjectFollowerEventPublisher extends EventPublisher<ProjectFollowerEventDto> {
    @Value("${spring.data.redis.channels.follower_project.name}")
    private String channel;

    public ProjectFollowerEventPublisher(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    protected String getChannel() {
        return channel;
    }
}
