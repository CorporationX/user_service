package school.faang.user_service.config.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class UserServiceConfig {

    @Value("${user-service.thread-pool-size}")
    private int threadPool;

    @Bean
    public ExecutorService completableFutureExecutor() {
        return Executors.newFixedThreadPool(threadPool);
    }
}
