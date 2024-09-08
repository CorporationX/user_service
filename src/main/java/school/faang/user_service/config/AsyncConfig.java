package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig  {

    @Value("${executor.threadsNumber}")
    private int threadsNumber;

    @Bean
    public ExecutorService executor() {
        return Executors.newFixedThreadPool(threadsNumber);
    }
}
