package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${spring.async.core-pool-size}")
    private int corePoolSize;

    @Value("${spring.async.max-pool-size}")
    private int maxPoolSize;

    @Value("${spring.async.queue-capacity}")
    private int queueCapacity;

    @Value("${spring.async.thread-name-prefix}")
    private String prefix;

    @Bean("taskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadNamePrefix(prefix);
        taskExecutor.initialize();
        return taskExecutor;
    }

}
