package school.faang.user_service.config.executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class RemoveEventsExecutorConfig {
    @Value("${scheduler.removeEventsExecutor.corePoolSize}")
    private int corePoolSize;

    @Value("${scheduler.removeEventsExecutor.maxPoolSize}")
    private int maxPoolSize;

    @Value("${scheduler.removeEventsExecutor.queueCapacity}")
    private int queueCapacity;

    @Bean(name = "removeEventsExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("RemoveEventsExecutor-");
        executor.initialize();
        return executor;
    }
}
