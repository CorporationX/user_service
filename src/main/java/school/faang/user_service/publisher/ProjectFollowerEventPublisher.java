package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProjectFollowerEvent;
import school.faang.user_service.mapper.JsonMapper;

@Component
public class ProjectFollowerEventPublisher extends AbstractPublisher<ProjectFollowerEvent> {
    public ProjectFollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                         @Value("${spring.data.redis.channels.project-follower-channel.name}") String channelName,
                                         JsonMapper<ProjectFollowerEvent> jsonMapper) {
        super(redisTemplate, channelName, jsonMapper);
    }
}
