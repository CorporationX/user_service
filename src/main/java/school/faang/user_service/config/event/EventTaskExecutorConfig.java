package school.faang.user_service.config.event;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@ConfigurationPropertiesScan
@EnableAsync
@RequiredArgsConstructor
public class EventTaskExecutorConfig {
    private final EventTaskExecutorParams params;

    @Bean
    public Executor eventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(params.getCorePoolSize());
        executor.setMaxPoolSize(params.getMaxPoolSize());
        executor.setQueueCapacity(params.getQueueCapacity());
        executor.setThreadNamePrefix(params.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
