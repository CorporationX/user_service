package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.FollowerEventDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper mapper;

    private final ChannelTopic topic;

    public void sendEvent(FollowerEventDto event) {
        log.info("User subscription event sending started");
            String json = mapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic.getTopic(),json);

        log.info("User subscription event sending finished");
    }
}