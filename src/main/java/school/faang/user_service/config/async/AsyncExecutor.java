package school.faang.user_service.config.async;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableConfigurationProperties(PropertiesOfAsyncThreads.class)
@RequiredArgsConstructor
public class AsyncExecutor {

    private final PropertiesOfAsyncThreads propertiesOfAsyncThreads;

    @Bean(name = "poolThreads")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(propertiesOfAsyncThreads.getThreadPoolSize());
        executor.setQueueCapacity(propertiesOfAsyncThreads.getQueueCapacity());
        executor.initialize();
        return executor;
    }
}
