package school.faang.user_service.config.async.promotion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class PromotionTaskServiceAsyncConfig {
    @Value("${app.async-config.promotion-task-service.core_pool_size}")
    private int corePoolSize;

    @Value("${app.async-config.promotion-task-service.max_pool_size}")
    private int maxPoolSize;

    @Value("${app.async-config.promotion-task-service.queue_capacity}")
    private int queueCapacity;

    @Value("${app.async-config.promotion-task-service.thread_name_prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor promotionTaskServicePool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
