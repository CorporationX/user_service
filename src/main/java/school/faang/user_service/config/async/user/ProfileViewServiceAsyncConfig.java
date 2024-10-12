package school.faang.user_service.config.async.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class ProfileViewServiceAsyncConfig {
    @Value("${app.async-config.profile-view-service.core_pool_size}")
    private int corePoolSize;

    @Value("${app.async-config.profile-view-service.max_pool_size}")
    private int maxPoolSize;

    @Value("${app.async-config.profile-view-service.queue_capacity}")
    private int queueCapacity;

    @Value("${app.async-config.profile-view-service.thread_name_prefix}")
    private String threadNamePrefix;

    @Bean
    public Executor profileViewServicePool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
