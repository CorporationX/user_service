package school.faang.user_service.config.executor;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {

    ExecutorService executorService;

    @Bean
    ExecutorService executorService() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        return executorService;
    }

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
    }
}