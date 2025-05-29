package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import school.faang.user_service.kafka.AnalyticsEvent;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisAnalyticsService {
    private final RedisTemplate<String, String> redisTemplate;
    private final AnalyticsProperties analyticsProperties;
    private final DefaultRedisScript<Long> counterScript;

    public long processEvent(List<AnalyticsEvent> events) {
        log.info("counterThreshold = {}", analyticsProperties.getCounterThreshold());
        AnalyticsEvent analyticsEvent = events.get(0);

        redisTemplate.opsForValue().increment(String.format("%s%d",
                analyticsEvent.getEventType().name(),
                analyticsEvent.getReceiverId()));

        //event:id
        //profile:id

        redisTemplate.executePipelined()


        if (!analyticsProperties.getAllowed().contains(evt.getEventType())) {
            return -1;
        }

        String key = String.format("%s:%d",
                evt.getEventType().name(),
                evt.getReceiverId()
        );
        Long counter = redisTemplate.execute(
                counterScript,
                Collections.singletonList(key),
                String.valueOf(analyticsProperties.getCounterThreshold()));

        if (counter == null) {
            log.error("Lua script returned null for key {}", key);
            throw new IllegalStateException("Redis script failed");
        }

        return counter;
    }

    public List<Object> incrementWithPipelineViaCallback(List<AnalyticsEvent> events) {
        return redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;
            for (AnalyticsEvent e : events) {
                String key = String.format("%s%d", e.getEventType().name(), e.getReceiverId());
                stringConn.incr(key);
            }
            return null;
        });
    }
}
