package school.faang.user_service.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    @Value("${scheduler.thread-pool-size}")
    private int numThreads;

    @Bean
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(numThreads);
    }
}
