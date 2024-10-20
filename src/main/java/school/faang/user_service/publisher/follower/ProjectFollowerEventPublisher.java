package school.faang.user_service.publisher.follower;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.projectfollower.ProjectFollowerEvent;
import school.faang.user_service.publisher.MessagePublisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectFollowerEventPublisher implements MessagePublisher<ProjectFollowerEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic projectFollowerTopic;

    public void publish(ProjectFollowerEvent event) {
        redisTemplate.convertAndSend(projectFollowerTopic.getTopic(), event);
    }
}