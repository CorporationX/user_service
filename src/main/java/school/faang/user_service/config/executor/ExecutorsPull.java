package school.faang.user_service.config.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorsPull {
    @Bean
    public ExecutorService getPull() {
       // new ThreadPoolExecutor(50, 100, 1L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100));
        return Executors.newFixedThreadPool(100);
    }
}
