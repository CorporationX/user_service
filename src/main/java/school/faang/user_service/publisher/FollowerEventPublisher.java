package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.FollowerEvent;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher extends EventPublisher<FollowerEvent> {

    private final ChannelTopic channelTopic;

    public void publish(FollowerEvent userFollowDto) {
        convertAndSend(userFollowDto, channelTopic.getTopic());
    }
}
