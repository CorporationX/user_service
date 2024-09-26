package school.faang.user_service.repository.cache;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import school.faang.user_service.dto.user.UserDto;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private static final String CACHE_PREFIX = "user:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${cache.post-ttl-seconds}")
    private long timeToLive;

    public void save(long userId, UserDto user) {
        String key = CACHE_PREFIX + userId;
        redisTemplate.opsForValue()
                .set(key, user, timeToLive, TimeUnit.SECONDS);
    }

    public Optional<UserDto> getUser(long userId) {
        String key = CACHE_PREFIX + userId;
        return Optional.ofNullable((UserDto) redisTemplate.opsForValue().get(key));
    }
}
