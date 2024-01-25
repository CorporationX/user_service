package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEvent;

@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEvent> {
    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerChannelName;

    public void publish(FollowerEvent event) {
        super.publish(event, followerChannelName);
    }
}
