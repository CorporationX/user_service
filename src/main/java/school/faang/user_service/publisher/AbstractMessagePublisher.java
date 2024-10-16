package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class AbstractMessagePublisher<T> implements MessagePublisher<T> {

    protected final RedisTemplate<String, T> redisTemplate;
    protected final ObjectMapper objectMapper;

    @Override
    public void publish(T eventMessage) {
        redisTemplate.convertAndSend(getTopic().getTopic(), eventMessage);

        log.info("Successfully published message: {} to channel: {}", eventMessage, getTopic().getTopic());
    }

    protected abstract ChannelTopic getTopic();
}

