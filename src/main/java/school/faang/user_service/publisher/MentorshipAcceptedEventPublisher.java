package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipAcceptedEvent;
@Component
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher implements MessagePublisher<MentorshipAcceptedEvent>{
    private final RedisTemplate<String,Object> redisTemplate;
    @Value("${spring.data.redis.channels.mentorship_accepted_channel.name}")
    private ChannelTopic channelTopic;
    @Override
    public void publish(MentorshipAcceptedEvent event) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), event);
    }
}
