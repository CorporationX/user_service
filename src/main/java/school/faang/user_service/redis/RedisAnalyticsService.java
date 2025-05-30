package school.faang.user_service.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.kafka.AnalyticsEvent;
import school.faang.user_service.kafka.EventType;
import school.faang.user_service.kafka.KafkaTopics;
import school.faang.user_service.kafka.producer.DataSender;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisAnalyticsService {
    private final RedisTemplate<String, String> redisTemplate;
    private final AnalyticsProperties analyticsProperties;
    private final DataSender dataSender;
    private final KafkaTopics kafkaTopics;

    public void incrementEventsCounter(List<AnalyticsEvent> events) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringConn = (StringRedisConnection) connection;
            for (AnalyticsEvent e : events) {
                if (analyticsProperties.getAllowed().contains(e.getEventType())) {
                    String zsetName = e.getEventType().name();
                    String member = String.format("%s:%d", e.getEventType().name(), e.getReceiverId());
                    stringConn.zIncrBy(zsetName, 1.0, member);
                }
            }
            return null;
        });
    }

    public Map<Long, Long> getIdsAboveThreshold(EventType eventType) {
        log.info("getIdsAboveThreshold method is called. counterThreshold = {}", analyticsProperties.getCounterThreshold());
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().rangeByScoreWithScores(eventType.name(),
                analyticsProperties.getCounterThreshold(), Double.MAX_VALUE);

        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Long> result = new HashMap<>(tuples.size());
        for (ZSetOperations.TypedTuple<String> t : tuples) {
            String member = t.getValue();
            Long score = t.getScore().longValue();
            String[] parts = member.split(":", 2);
            Long id = Long.valueOf(parts[1]);

            result.put(id, score);
        }

        return result;
    }

    @Retryable(
            retryFor = RedisConnectionFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void removeProcessedKeys(EventType eventType, Set<Long> keys) {
        log.info("removeProcessedKeys method is called");
        redisTemplate.opsForZSet().remove(eventType.name(),
                keys.stream()
                        .map(id -> String.format("%s:%d", eventType.name(), id))
                        .toList());
    }

    @Recover
    public void recover(RedisConnectionFailureException e,
                        EventType eventType,
                        Set<Long> keys) {
        log.error("Failed to remove keys after retries: {}, {}", eventType, keys, e);
        dataSender.send(kafkaTopics.getRedisRetryErrorTopic(), keys.stream().toList());
    }
}
