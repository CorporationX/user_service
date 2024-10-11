package school.faang.user_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private int port;
    private String host;
    private Map<String, String> channels;
}
