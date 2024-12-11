package school.faang.user_service.service;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

public interface RedisTransactional {

    RedisTemplate<String, Object> getRedisTemplate();

    default void executeRedisTransaction(Runnable transaction) {
        RedisTemplate<String, Object> redisTemplate = getRedisTemplate();
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.multi();
            transaction.run();
            connection.exec();
            return null;
        });
    }
}
