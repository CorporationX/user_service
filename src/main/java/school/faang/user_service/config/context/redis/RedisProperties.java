package school.faang.user_service.config.context.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(Integer port, String host, String usersBanTopic) {
}
