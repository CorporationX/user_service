package school.faang.user_service.config.threadPool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {

    @Value("${premium.pool-size}")
    private int fixedPoolSize;

    @Bean
    public ExecutorService fixedThreadPools(){
        return Executors.newFixedThreadPool(fixedPoolSize);
    }
}
