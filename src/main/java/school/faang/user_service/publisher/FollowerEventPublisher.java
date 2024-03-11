package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.FollowerEventDto;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher extends EventPublisher<FollowerEventDto> {

    private final ChannelTopic channelTopic;

    public void publish(FollowerEventDto userFollowDto) {
        convertAndSend(userFollowDto, channelTopic.getTopic());
    }
}
