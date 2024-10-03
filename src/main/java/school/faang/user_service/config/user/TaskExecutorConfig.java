package school.faang.user_service.config.user;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class TaskExecutorConfig {

    private final TaskExecutorParams taskExecutorParams;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutorParams.getCorePoolSize());
        executor.setMaxPoolSize(taskExecutorParams.getMaxPoolSize());
        executor.setQueueCapacity(taskExecutorParams.getQueueCapacity());
        executor.setThreadNamePrefix(taskExecutorParams.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
