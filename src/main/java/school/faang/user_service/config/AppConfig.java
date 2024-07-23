package school.faang.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import school.faang.user_service.properties.ExecutorProperties;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
public class AppConfig {
    private final ExecutorProperties executorProperties;

    @Bean(name = "premiumExpirationExecutor")
    public Executor premiumExecutor() {
        return createExecutor(executorProperties.getPremiumExpiration());
    }

    @Bean(name = "promotionExpirationExecutor")
    public Executor promotionExecutor() {
        return createExecutor(executorProperties.getPromotionExpiration());
    }

    private Executor createExecutor(ExecutorProperties.PoolConfig config) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        executor.setThreadNamePrefix(config.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
