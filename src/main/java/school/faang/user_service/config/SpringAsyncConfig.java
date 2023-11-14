package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@EnableAsync
public class SpringAsyncConfig {

    @Value("${spring.async.thread-pool.settings.core-pool-size}")
    private int corePoolSize;
    @Value("${spring.async.thread-pool.settings.max-pool-size}")
    private int maxPoolSize;
    @Value("${spring.async.thread-pool.settings.queue-capacity}")
    private int queueCapacity;

    @Bean("kafkaThreadPool")
    public Executor kafkaThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.initialize();
        return executor;
    }
}
