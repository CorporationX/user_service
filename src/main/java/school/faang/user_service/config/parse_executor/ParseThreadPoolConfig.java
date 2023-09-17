package school.faang.user_service.config.parse_executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
public class ParseThreadPoolConfig {

    @Bean("parseExecutorService")
    public ExecutorService parseExecutorService() {
        return Executors.newCachedThreadPool();
    }
}
