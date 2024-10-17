package school.faang.user_service.config.threadPool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${thread-pool-config.size}")
    private int threadPoolSize;

    @Bean
    public ExecutorService threadPools(){
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}
