package school.faang.user_service.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private String host;
    private int port;
    private Channels channels;
}

@Data
class Channels {
    private String recommendationChannel;
    private String subscriptionChannel;
}
