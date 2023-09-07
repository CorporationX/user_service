package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractEventPublisher {
    protected final ObjectMapper objectMapper;
    protected final RedisMessagePublisher redisMessagePublisher;
}