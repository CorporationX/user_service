package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.ProjectFollowerEvent;

@Service
public class ProjectFollowerEventPublisher extends AbstractEventPublisher<ProjectFollowerEvent> {


    public ProjectFollowerEventPublisher(RedisTemplate<String, Object> redisTemplate, @Value("${spring.data.redis.channels.follower_view.name}")  String topic, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, topic);
    }

    public void sendEvent(ProjectFollowerEvent projectFollowerEvent) {
        publish(projectFollowerEvent);
    }
}
