package school.faang.user_service.redis.promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import school.faang.user_service.kafka.events.AnalyticsEvent;
import school.faang.user_service.kafka.events.AnalyticsEventType;
import school.faang.user_service.kafka.producer.DataSender;
import school.faang.user_service.kafka.producer.KafkaTopics;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PromotionAnalyticsCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private final PromotionAnalyticsProperties promotionProperties;
    private final DataSender dataSender;
    private final KafkaTopics kafkaTopics;

    public void incrementEventsCounter(List<AnalyticsEvent> events) {
        log.info("incrementEventsCounter method is called. events size = {}", events.size());
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringConn = new DefaultStringRedisConnection(connection);
            for (AnalyticsEvent e : events) {
                if (promotionProperties.getAllowed().contains(e.getAnalyticsEventType())) {
                    String zsetName = e.getAnalyticsEventType().name();
                    String member = String.format("%s:%d", e.getAnalyticsEventType().name(), e.getReceiverId());
                    stringConn.zIncrBy(zsetName, 1.0, member);
                }
            }
            return null;
        });
        log.info("incrementEventsCounter method has been completed");
    }

    public Map<Long, Long> getIdsScoreAboveThreshold(AnalyticsEventType analyticsEventType) {
        log.info("Executing getIdsAboveThreshold method for Event type = {}, counter threshold = {}",
                analyticsEventType, promotionProperties.getCounterThreshold());
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .rangeByScoreWithScores(analyticsEventType.name(), promotionProperties.getCounterThreshold(), Double.MAX_VALUE);

        if (tuples == null || tuples.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Long> result = new HashMap<>(tuples.size());
        for (ZSetOperations.TypedTuple<String> t : tuples) {
            String member = t.getValue();
            Long score = t.getScore().longValue();
            String[] parts = member.split(":");
            Long id = Long.valueOf(parts[1]);

            result.put(id, score);
        }
        log.info("getIdsAboveThreshold method returns result size = {}", result.size());
        return result;
    }

    @Retryable(
            retryFor = RedisConnectionFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void removeProcessedKeys(AnalyticsEventType analyticsEventType, @NotNull List<Long> keys) {
        log.info("removeProcessedKeys method is called for Event type = {}, keys size = {}", analyticsEventType, keys.size());
        redisTemplate.opsForZSet().remove(analyticsEventType.name(),
                keys.stream()
                        .map(id -> String.format("%s:%d", analyticsEventType.name(), id))
                        .toArray());
        log.info("removeProcessedKeys method removed keys successfully");
    }

    @Recover
    public void recover(RedisConnectionFailureException e,
                        AnalyticsEventType analyticsEventType,
                        Set<Long> keys) {
        log.error("Failed to remove keys after retries: {}, {}", analyticsEventType, keys, e);
        dataSender.send(kafkaTopics.getRedisRetryErrorTopic(), keys.stream().toList());
    }
}
