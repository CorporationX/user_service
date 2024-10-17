package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.ProjectFollowerEvent;

@Component
public class ProjectFollowerEventPublisher extends RedisEventPublisher<ProjectFollowerEvent> {
    public ProjectFollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                         ObjectMapper objectMapper,
                                         @Qualifier("projectFollowerChannelTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}