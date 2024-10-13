package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

    @Value("redis.host")
    private String host;

    @Value("redis.port")
    private String port;

    RedisTemplate<String, Object> redisTemplate() {

    }
}
