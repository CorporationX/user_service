package school.faang.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Slf4j
public class ExecutionPoolPremiumRemoveConfig {
    @Value("${task.expired-premium-remove.thread-pool.core-pool-size}")
    private int corePoolSize;
    @Value("${task.expired-premium-remove.thread-pool.max-pool-capacity}")
    private int maxPoolCapacity;
    @Value("${task.expired-premium-remove.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Bean("premiumRemoveTaskExecutor")
    public ThreadPoolTaskExecutor premiumRemoveTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolCapacity);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("PremiumRemoveTaskThread-");
        executor.setRejectedExecutionHandler((r, exec) -> log.warn("Task rejected, thread pool is full and queue is also full"));
        executor.initialize();
        return executor;
    }
}
