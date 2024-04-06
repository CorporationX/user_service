package school.faang.user_service.config.executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorsConfig {

    @Value("${thread-pool.thread-count}")
    private Integer threadCount;

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(threadCount);
    }
}
