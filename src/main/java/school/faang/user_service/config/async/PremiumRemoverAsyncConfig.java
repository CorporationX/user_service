package school.faang.user_service.config.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class PremiumRemoverAsyncConfig {

    @Value("${premium.removal.thread-pool.core-pool-size}")
    private int corePoolSize;
    @Value("${premium.removal.thread-pool.max-pool-capacity}")
    private int maxPoolCapacity;
    @Value("${premium.removal.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Bean("premiumRemovalAsyncExecutor")
    public Executor premiumRemovalAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolCapacity);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("PremiumRemovalAsyncExecutor-");
        executor.setRejectedExecutionHandler((runnable, altExecutor) -> {
            log.warn("Task rejected: {}", runnable.toString());
            new ThreadPoolExecutor.CallerRunsPolicy().rejectedExecution(runnable, altExecutor);
        });
        executor.initialize();
        return executor;
    }
}
