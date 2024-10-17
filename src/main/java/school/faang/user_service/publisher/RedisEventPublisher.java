package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.config.RedisConfig.MessagePublisher;

@Slf4j
@RequiredArgsConstructor
public abstract class RedisEventPublisher<T> implements MessagePublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic channelTopic;

    public void publish(T redisEvent) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), redisEvent);
    }
}

