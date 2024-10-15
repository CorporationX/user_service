package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.ProjectFollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectSubscriptionPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followProjectTopic;

    public void publish(ProjectFollowerEvent projectFollowerEvent){
        redisTemplate.convertAndSend(followProjectTopic.getTopic(), projectFollowerEvent);
    }
}
