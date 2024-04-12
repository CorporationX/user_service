package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.follower.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements MessagePublisher<FollowerEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(FollowerEvent event) throws JsonProcessingException {
        redisTemplate.convertAndSend(followerTopic.getTopic(), objectMapper.writeValueAsString(event));
    }
}
