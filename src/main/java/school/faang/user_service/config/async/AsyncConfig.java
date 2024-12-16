package school.faang.user_service.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {
    @Value("${application.premium.threads-number}")
    private int numberOfThreads;

    @Bean
    public ExecutorService removeExpiredPremiumAccess(){
        return Executors.newFixedThreadPool(numberOfThreads);
    }

    @Bean
    public ExecutorService removeExpiredEvent() {
        return Executors.newFixedThreadPool(numberOfThreads);
    }
}
