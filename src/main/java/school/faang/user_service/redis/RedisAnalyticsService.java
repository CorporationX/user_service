package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.kafka.AnalyticsEvent;
import school.faang.user_service.kafka.EventType;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisAnalyticsService {
    private final RedisTemplate<String, String> redisTemplate;
    private final AnalyticsProperties analyticsProperties;

    public void processEvent(List<AnalyticsEvent> events) {
        log.info("counterThreshold = {}", analyticsProperties.getCounterThreshold());
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;
            for (AnalyticsEvent e : events) {
                if (e.getEventType() == EventType.PROFILE_VIEW || e.getEventType() == EventType.EVENT_VIEW) {
                    String zsetName = e.getEventType().name();
                    String member = String.format("%s:%d", e.getEventType().name(), e.getReceiverId());
                    stringConn.zIncrBy(zsetName, 1.0, member);
                }
            }
            return null;
        });
    }
}
