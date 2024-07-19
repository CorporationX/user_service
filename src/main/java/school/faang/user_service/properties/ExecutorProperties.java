package school.faang.user_service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "executor")
@Component
@Getter
@Setter
public class ExecutorProperties {
    private PoolConfig premiumExpiration;
    private PoolConfig promotionExpiration;

    @Getter
    @Setter
    public static class PoolConfig {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadNamePrefix;
    }
}
