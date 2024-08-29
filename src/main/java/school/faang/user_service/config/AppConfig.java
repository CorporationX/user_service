package school.faang.user_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppConfig {

    @Bean
    public ExecutorService executorService(@Value("${premium-remover.amount-thread-executor}") int threadAmount) {
        return Executors.newFixedThreadPool(threadAmount);
    }
}
