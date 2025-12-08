package school.faang.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@EnableScheduling
@Configuration
public class AsyncConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService(@Value("${scheduler.thread-pool-size}") int threadPoolSize) {
        log.info("Spring создаёт ExecutorService");
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}