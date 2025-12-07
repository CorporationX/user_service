package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class PremiumCacheService {

    private static final String PREMIUM_ACTIVE_KEY = "premium:active:";

    private final StringRedisTemplate stringRedisTemplate;

    public boolean isActive(long userId) {
        String value = stringRedisTemplate.opsForValue().get(buildKey(userId));
        return value != null;
    }

    public void setActiveUntil(long userId, LocalDateTime endDate) {
        if (endDate == null) {
            return;
        }
        long nowEpochMillis = LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli();
        long endEpochMillis = endDate.toInstant(ZoneOffset.UTC).toEpochMilli();
        long remainingMillis = Math.max(endEpochMillis - nowEpochMillis, 0);
        if (remainingMillis == 0) {
            evict(userId);
            return;
        }
        Duration ttl = Duration.ofMillis(remainingMillis);
        stringRedisTemplate.opsForValue().set(buildKey(userId), "1", ttl);
    }

    public void evict(long userId) {
        stringRedisTemplate.delete(buildKey(userId));
    }

    private String buildKey(long userId) {
        return PREMIUM_ACTIVE_KEY + userId;
    }
}


