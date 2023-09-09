package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.redis.RedisMessagePublisher;

@RequiredArgsConstructor
public abstract class AbstractEventPublisher {
    protected final EventMapper eventMapper;
    protected final ObjectMapper objectMapper;
    protected final RedisMessagePublisher redisMessagePublisher;
}
